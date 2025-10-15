package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.DTO.editor.DocElementCatalogCreationDTO;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.DTO.editor.EspecificacaoDocDTO;
import com.squad13.apimonolito.exceptions.AssociationAlreadyExistsException;
import com.squad13.apimonolito.exceptions.InvalidDocumentTypeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.mongo.editor.*;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.util.CatalogSearch;
import com.squad13.apimonolito.util.DocElementFactory;
import com.squad13.apimonolito.util.DocumentSearch;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.enums.LocalEnum;
import com.squad13.apimonolito.util.mappers.CatalogMapper;
import com.squad13.apimonolito.util.mappers.EditorMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class EspecificacaoService {

    private final DocElementFactory docElementFactory;

    private final EmpreendimentoRepository empRepository;
    private final EspecificacaoDocRepository especRepository;

    private final LocalDocRepository localDocRepository;
    private final CatalogMapper catalogMapper;
    private final EditorMapper editorMapper;
    private final CatalogSearch catalogSearch;
    private final DocumentSearch documentSearch;
    private final AmbienteDocElementRepository ambienteDocRepository;
    private final MaterialDocElementRepository materialDocRepository;
    private final MarcaDocElementRepository marcaDocRepository;
    private final ItemDocElementRepository itemDocRepository;

    public List<EspecificacaoDoc> getAll() {
        return especRepository.findAll();
    }

    public EspecificacaoDoc getById(String id) {
        return especRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o ID: " + id));
    }

    public EspecificacaoDoc create(EspecificacaoDocDTO dto) {
        if (!empRepository.existsById(dto.empId())) {
            throw new ResourceNotFoundException(
                    "Empreendimento não encontrado com o ID: " + dto.empId()
            );
        }

        return switch (dto.initType()) {
            case AVULSO -> createEspecificacaoAvulso(dto);
            case PADRAO -> createFromPadrao(dto); // TODO: implementar
            case IMPORT -> createFromImport(dto); // TODO: implementar
        };
    }

    private EspecificacaoDoc createEspecificacaoAvulso(EspecificacaoDocDTO dto) {
        EspecificacaoDoc espec = new EspecificacaoDoc();
        espec.setName(dto.name());
        espec.setEmpreendimentoId(dto.empId());
        espec.setDesc(dto.desc());
        espec.setObs(dto.obs());
        especRepository.save(espec);

        List<LocalDoc> locais = Arrays.stream(LocalEnum.values())
                .map(localEnum -> {
                    LocalDoc local = new LocalDoc();
                    local.setEspecificacaoDoc(espec);
                    local.setLocal(localEnum);
                    return localDocRepository.save(local);
                })
                .toList();

        espec.setLocais(locais);

        return especRepository.save(espec);
    }

    private EspecificacaoDoc createFromPadrao(EspecificacaoDocDTO dto) {
        // TODO: implementar inicialização por padrão
        throw new UnsupportedOperationException("Inicialização por PADRAO ainda não implementada");
    }

    private EspecificacaoDoc createFromImport(EspecificacaoDocDTO dto) {
        // TODO: implementar inicialização por importação
        throw new UnsupportedOperationException("Inicialização por IMPORT ainda não implementada");
    }

    public DocElement createElement(String especId, DocElementCatalogCreationDTO dto) {
        EspecificacaoDoc espec = getById(especId);

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
        EspecificacaoDoc espec = getById(especId);

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
        especRepository.save(espec);
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
        boolean exists = espec.getLocais().stream()
                .filter(l -> l.getLocal().equals(ambiente.getLocal()))
                .flatMap(l -> l.getAmbienteDocList().stream())
                .filter(Objects::nonNull)
                .anyMatch(a -> a.getName().equalsIgnoreCase(ambiente.getName()));
        if (exists) throw new ResourceAlreadyExistsException("Ambiente duplicado: " + ambiente.getName());
    }

    private void checkDuplicateMaterial(EspecificacaoDoc espec, MaterialDocElement material) {
        boolean exists = espec.getMateriais().stream()
                .filter(Objects::nonNull)
                .anyMatch(m -> m.getName().equalsIgnoreCase(material.getName()));
        if (exists) throw new ResourceAlreadyExistsException("Material duplicado: " + material.getName());
    }
}