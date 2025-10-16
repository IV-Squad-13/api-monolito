package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.DTO.editor.*;
import com.squad13.apimonolito.exceptions.AssociationAlreadyExistsException;
import com.squad13.apimonolito.exceptions.InvalidDocumentTypeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.mongo.editor.*;
import com.squad13.apimonolito.util.CatalogSearch;
import com.squad13.apimonolito.util.DocElementFactory;
import com.squad13.apimonolito.util.DocumentSearch;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.mappers.EditorMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class DocElementService {

    private final DocElementFactory  docElementFactory;

    private final SynchronizationService syncService;

    private final CatalogSearch catalogSearch;
    private final DocumentSearch documentSearch;

    private final EditorMapper editorMapper;

    private final EspecificacaoDocRepository especificacaoDocRepository;
    private final LocalDocRepository localDocRepository;
    private final AmbienteDocElementRepository ambienteDocRepository;
    private final ItemDocElementRepository itemDocRepository;
    private final MaterialDocElementRepository materialDocRepository;
    private final MarcaDocElementRepository marcaDocRepository;

    public List<? extends DocElement> getAll(DocElementParams params) {
        return documentSearch.findDocuments(params.getType().getDocElement());
    }

    private EspecificacaoDoc getByEspecificacaoId(String id) {
        return especificacaoDocRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o ID: " + id));
    }

    public DocElement createElement(String especId, DocElementCatalogCreationDTO dto) {
        EspecificacaoDoc espec = getByEspecificacaoId(especId);

        Object catalogEntity = catalogSearch.findInCatalog(dto.elementId(), dto.type().getCatalogEntity());
        if (catalogEntity == null)
            throw new ResourceNotFoundException("Entidade de catálogo não encontrada: " + dto.elementId());

        DocElement newElement = editorMapper.fromCatalog(espec, catalogEntity, dto.type());
        saveElement(espec, newElement, dto.type());

        if (dto.associatedId() != null)
            associateIfNeeded(dto, newElement);

        return newElement;
    }

    public DocElement createRawElement(String especId, DocElementDTO dto) {
        EspecificacaoDoc espec = getByEspecificacaoId(especId);

        DocElement newElement = docElementFactory.create(espec, dto);
        saveElement(espec, newElement, dto.getDocType());

        return newElement;
    }

    private void saveElement(EspecificacaoDoc espec, DocElement element, DocElementEnum type) {
        switch (type) {
            case AMBIENTE -> saveAmbiente(espec, (AmbienteDocElement) element);
            case ITEM -> saveItem((ItemDocElement) element);
            case MATERIAL -> saveMaterial(espec, (MaterialDocElement) element);
            case MARCA -> saveMarca((MarcaDocElement) element);
            default -> throw new InvalidDocumentTypeException("Tipo de elemento não suportado: " + type);
        }
    }

    private void saveAmbiente(EspecificacaoDoc espec, AmbienteDocElement ambiente) {
        checkDuplicateAmbiente(espec, ambiente);
        ambienteDocRepository.save(ambiente);
        LocalDoc local = espec.getLocais().stream()
                .filter(Objects::nonNull)
                .filter(l -> l.getLocal().equals(ambiente.getLocal()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Local não encontrado: " + ambiente.getLocal()));
        ambiente.setPrevId(local.getId());
        local.getAmbienteDocList().add(ambiente);
        localDocRepository.save(local);
    }

    private void saveItem(ItemDocElement item) {
        if (item.getPrevId() != null) {
            AmbienteDocElement ambiente = documentSearch.findInDocument(item.getPrevId(), AmbienteDocElement.class);
            associateItemToAmbiente(ambiente, item);
        } else {
            itemDocRepository.save(item);
        }
    }

    private void saveMaterial(EspecificacaoDoc espec, MaterialDocElement material) {
        checkDuplicateMaterial(espec, material);
        materialDocRepository.save(material);
        espec.getMateriais().add(material);
        especificacaoDocRepository.save(espec);
    }

    private void saveMarca(MarcaDocElement marca) {
        if (marca.getPrevId() != null) {
            MaterialDocElement material = documentSearch.findInDocument(marca.getPrevId(), MaterialDocElement.class);
            associateMarcaToMaterial(material, marca);
        } else {
            marcaDocRepository.save(marca);
        }
    }

    private void associateIfNeeded(DocElementCatalogCreationDTO dto, DocElement doc) {
        DocElementEnum assocType = switch (dto.type()) {
            case ITEM -> DocElementEnum.AMBIENTE;
            case MARCA -> DocElementEnum.MATERIAL;
            default -> throw new UnsupportedOperationException(
                    "Associação não suportada para tipo: " + dto.type()
            );
        };

        DocElement associated = documentSearch.findInDocument(dto.associatedId(), assocType.getDocElement());
        if (associated == null) {
            throw new ResourceNotFoundException("Elemento associado não encontrado: " + dto.associatedId());
        }

        switch (associated) {
            case AmbienteDocElement ambiente when doc instanceof ItemDocElement item -> {
                if (ambiente.getItemDocList().contains(item))
                    throw new AssociationAlreadyExistsException("O item já está associado ao ambiente");
                ambiente.getItemDocList().add(item);
                ambienteDocRepository.save(ambiente);
            }
            case MaterialDocElement material when doc instanceof MarcaDocElement marca -> {
                if (material.getMarcaDocList().contains(marca))
                    throw new AssociationAlreadyExistsException("A marca já está associada ao material");
                material.getMarcaDocList().add(marca);
                materialDocRepository.save(material);
            }
            default -> throw new UnsupportedOperationException("Tipo de associação inválido: " + dto.type());
        }
    }

    private void associateItemToAmbiente(AmbienteDocElement ambiente, ItemDocElement item) {
        if (ambiente.getItemDocList().stream()
                .filter(Objects::nonNull)
                .anyMatch(existing -> existing.getName().equalsIgnoreCase(item.getName())))
            throw new ResourceAlreadyExistsException("Item '" + item.getName() + "' já existe em " + ambiente.getName());

        itemDocRepository.save(item);
        ambiente.getItemDocList().add(item);
        ambienteDocRepository.save(ambiente);
    }

    private void associateMarcaToMaterial(MaterialDocElement material, MarcaDocElement marca) {
        if (material.getMarcaDocList().stream()
                .filter(Objects::nonNull)
                .anyMatch(existing -> existing.getName().equalsIgnoreCase(marca.getName())))
            throw new ResourceAlreadyExistsException("Marca '" + marca.getName() + "' já existe em " + material.getName());

        marcaDocRepository.save(marca);
        material.getMarcaDocList().add(marca);
        materialDocRepository.save(material);
    }

    private void checkDuplicateAmbiente(EspecificacaoDoc espec, AmbienteDocElement ambiente) {
        boolean exists = ambienteDocRepository.existsByNameAndLocalAndEspecificacaoDoc(
                ambiente.getName(),
                ambiente.getLocal(),
                espec
        );

        if (exists) {
            throw new ResourceAlreadyExistsException("Ambiente duplicado: " + ambiente.getName());
        }
    }

    private void checkDuplicateAmbiente(AmbienteDocElement ambiente) {
        boolean exists = ambienteDocRepository.existsByNameAndPrevId(
                ambiente.getName(),
                ambiente.getPrevId()
        );

        if (exists) {
            throw new ResourceAlreadyExistsException("Ambiente duplicado: " + ambiente.getName());
        }
    }

    private void checkDuplicateItem(AmbienteDocElement ambiente, ItemDocElement item) {
        boolean exists = itemDocRepository.existsByNameAndPrevIdAndEspecificacaoDoc(
                item.getName(),
                ambiente.getId(),
                item.getEspecificacaoDoc()
        );

        if (exists) {
            throw new ResourceAlreadyExistsException("Item duplicado: " + item.getName());
        }
    }

    private void checkDuplicateMaterial(EspecificacaoDoc espec, MaterialDocElement material) {
        boolean exists = materialDocRepository.existsByNameAndEspecificacaoDoc(
                material.getName(),
                espec
        );

        if (exists) {
            throw new ResourceAlreadyExistsException("Material duplicado: " + material.getName());
        }
    }

    private void checkDuplicateMarca(MaterialDocElement material, MarcaDocElement marca) {
        boolean exists = marcaDocRepository.existsByNameAndPrevIdAndEspecificacaoDoc(
                marca.getName(),
                material.getId(),
                marca.getEspecificacaoDoc()
        );

        if (exists) {
            throw new ResourceAlreadyExistsException("Marca duplicada: " + marca.getName());
        }
    }

    public DocElement update(String id, DocElementDTO dto) {
        return switch (dto.getDocType()) {
            case AMBIENTE -> updateAmbiente(id, (AmbienteDocDTO) dto);
            case ITEM -> updateItem(id, (ItemDocDTO) dto);
            case MATERIAL -> updateMaterial(id, (MaterialElementDTO) dto);
            case MARCA -> updateMarca(id, (MarcaElementDTO) dto);
            default -> throw new InvalidDocumentTypeException("Tipo de elemento não suportado: " + dto.getDocType());
        };
    }

    private AmbienteDocElement updateAmbiente(String id, AmbienteDocDTO dto) {
        AmbienteDocElement ambiente = ambienteDocRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ambiente não encontrado com o id informado: " + id)
                );

        if (dto.getName() != null && !dto.getName().isBlank()) {
            ambiente.setName(dto.getName());
        }

        if (dto.getLocal() != null) {
            ambiente.setLocal(dto.getLocal());

            if (dto.getPrevId() != null) {
                LocalDoc local = localDocRepository.findById(dto.getPrevId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Local não encontrado com o id informado: " + dto.getPrevId())
                        );

                if (ambiente.getPrevId() == null) {
                    ambiente.setPrevId(dto.getPrevId());
                } else {
                    LocalDoc prevLocal = localDocRepository.findById(ambiente.getPrevId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Local anterior não encontrado: " + ambiente.getPrevId())
                            );
                    prevLocal.getAmbienteDocList().remove(ambiente);
                }

                checkDuplicateAmbiente(ambiente);
                local.getAmbienteDocList().add(ambiente);
                localDocRepository.save(local);
            } else {
                EspecificacaoDoc espec = ambiente.getEspecificacaoDoc();

                LocalDoc local = espec.getLocais().stream()
                        .filter(Objects::nonNull)
                        .filter(l -> l.getLocal().equals(ambiente.getLocal()))
                        .findFirst()
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Local não encontrado: " + ambiente.getLocal())
                        );

                ambiente.setPrevId(local.getId());
                local.getAmbienteDocList().add(ambiente);
            }
        }

        ambiente.setInSync(syncService.inSync(ambiente, DocElementEnum.AMBIENTE));
        return ambienteDocRepository.save(ambiente);
    }

    private ItemDocElement updateItem(String id, ItemDocDTO dto) {
        ItemDocElement item = itemDocRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Item não encontrado com o id informado: " + id)
                );

        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }

        if (dto.getPrevId() != null) {
            AmbienteDocElement newAmbiente = ambienteDocRepository.findById(dto.getPrevId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Ambiente não encontrado com o id informado: " + dto.getPrevId())
                    );

            if (item.getPrevId() != null && !item.getPrevId().equals(dto.getPrevId())) {
                AmbienteDocElement oldAmbiente = ambienteDocRepository.findById(item.getPrevId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Ambiente anterior não encontrado: " + item.getPrevId())
                        );
                oldAmbiente.getItemDocList().remove(item);
                ambienteDocRepository.save(oldAmbiente);
            }

            checkDuplicateItem(newAmbiente, item);
            item.setPrevId(newAmbiente.getId());
            newAmbiente.getItemDocList().add(item);
            ambienteDocRepository.save(newAmbiente);
        }

        item.setInSync(syncService.inSync(item, DocElementEnum.ITEM));
        return itemDocRepository.save(item);
    }

    private MaterialDocElement updateMaterial(String id, MaterialElementDTO dto) {
        MaterialDocElement material = materialDocRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Material não encontrado com o id informado: " + id)
                );

        if (dto.getName() != null && !dto.getName().isBlank()) {
            material.setName(dto.getName());
        }

        EspecificacaoDoc espec = material.getEspecificacaoDoc();
        if (espec == null) {
            throw new ResourceNotFoundException("Especificação não associada ao material: " + id);
        }

        checkDuplicateMaterial(espec, material);

        material.setInSync(syncService.inSync(material, DocElementEnum.MATERIAL));
        return materialDocRepository.save(material);
    }

    private MarcaDocElement updateMarca(String id, MarcaElementDTO dto) {
        MarcaDocElement marca = marcaDocRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Marca não encontrada com o id informado: " + id)
                );

        if (dto.getName() != null && !dto.getName().isBlank()) {
            marca.setName(dto.getName());
        }

        if (dto.getPrevId() != null) {
            MaterialDocElement newMaterial = materialDocRepository.findById(dto.getPrevId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Material não encontrado com o id informado: " + dto.getPrevId())
                    );

            if (marca.getPrevId() != null && !marca.getPrevId().equals(dto.getPrevId())) {
                MaterialDocElement oldMaterial = materialDocRepository.findById(marca.getPrevId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Material anterior não encontrado: " + marca.getPrevId())
                        );
                oldMaterial.getMarcaDocList().remove(marca);
                materialDocRepository.save(oldMaterial);
            }

            checkDuplicateMarca(newMaterial, marca);
            marca.setPrevId(newMaterial.getId());
            newMaterial.getMarcaDocList().add(marca);
            materialDocRepository.save(newMaterial);
        }

        marca.setInSync(syncService.inSync(marca, DocElementEnum.MARCA));
        return marcaDocRepository.save(marca);
    }

    public void delete(String id, DocElementEnum type) {
        documentSearch.deleteWithReferences(id, type.getDocElement());
    }
}
