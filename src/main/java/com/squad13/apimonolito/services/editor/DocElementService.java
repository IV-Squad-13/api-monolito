package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.DTO.editor.*;
import com.squad13.apimonolito.DTO.editor.res.*;
import com.squad13.apimonolito.exceptions.AssociationAlreadyExistsException;
import com.squad13.apimonolito.exceptions.InvalidDocumentTypeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.mongo.editor.*;
import com.squad13.apimonolito.util.builder.DocElementBuilder;
import com.squad13.apimonolito.util.builder.MongoUpdateBuilder;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.mapper.EditorMapper;
import com.squad13.apimonolito.util.search.CatalogSearch;
import com.squad13.apimonolito.util.search.DocumentSearch;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DocElementService {

    private final DocElementBuilder docElementBuilder;
    private final DocElementBuilder docBuilder;
    private final MongoUpdateBuilder mongoUpdateBuilder;

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

    public List<? extends ResDocElementDTO> getAll(LoadDocumentParamsDTO params, DocElementEnum docType) {
        Aggregation agg = docBuilder.buildAggregation(params);
        return documentSearch.findWithAggregation(documentSearch.getCollection(docType), docType.getResDocDTO(), agg);
    }

    public ResDocElementDTO getById(ObjectId id, LoadDocumentParamsDTO params, DocElementEnum docType) {
        return search(params, new DocSearchParamsDTO(docType, id)).stream()
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(docType + " não encontrado para o ID: " + id)
                );
    }

    public List<? extends ResDocElementDTO> search(LoadDocumentParamsDTO loadParams, DocSearchParamsDTO searchParams) {
        DocElementEnum docType = searchParams.getDocType();
        Map<String, Object> filters = DocSearchParamsDTO.buildFilters(searchParams);
        List<MatchOperation> matchOperations = documentSearch.buildMatchOps(filters);

        Aggregation agg = docBuilder.buildAggregation(loadParams);
        return documentSearch.searchWithAggregation(
                documentSearch.getCollection(docType),
                docType.getResDocDTO(),
                matchOperations,
                agg
        );
    }

    public List<? extends DocElement> search(DocSearchParamsDTO searchParams) {
        DocElementEnum docType = searchParams.getDocType();
        Map<String, Object> filters = DocSearchParamsDTO.buildFilters(searchParams);
        List<MatchOperation> matchOperations = documentSearch.buildMatchOps(filters);

        Aggregation agg = docBuilder.buildAggregation(LoadDocumentParamsDTO.allFalse());
        return documentSearch.searchWithAggregation(
                documentSearch.getCollection(docType),
                docType.getDocElement(),
                matchOperations,
                agg
        );
    }

    private EspecificacaoDoc getSpecById(ObjectId id) {
        return especificacaoDocRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o ID: " + id));
    }

    private ObjectId newId() {
        return new ObjectId();
    }

    public ResDocElementDTO createElement(ObjectId especId, DocElementCatalogCreationDTO dto) {
        EspecificacaoDoc especificacaoDoc = getSpecById(especId);

        Object catalogEntity = catalogSearch.findInCatalog(dto.elementId(), dto.type().getCatalogEntity());
        if (catalogEntity == null)
            throw new ResourceNotFoundException("Entidade de catálogo não encontrada: " + dto.elementId());

        DocElement newElement = editorMapper.fromCatalog(especificacaoDoc.getId(), catalogEntity, null, dto.type());
        saveElement(especificacaoDoc, newElement, dto.type());

        if (dto.parentId() != null)
            associateIfNeeded(dto, newElement);

        return ResDocElementDTO.fromDoc(newElement, dto.type().getResDocSupplier());
    }

    public Map<DocElementEnum, List<? extends ResDocElementDTO>> createManyElements(ObjectId specId, BulkDocElementCatalogCreationDTO bulkDto) {
        List<DocElementCatalogCreationDTO> dtoList = bulkDto.elements();

        Map<DocElementEnum, List<Long>> elementFilters = dtoList.stream()
                .collect(Collectors.groupingBy(
                        DocElementCatalogCreationDTO::type,
                        Collectors.mapping(DocElementCatalogCreationDTO::elementId, Collectors.toList())
                ));

        Map<DocElementEnum, List<DocElement>> sortedDocs = elementFilters.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            DocElementEnum docType = entry.getKey();

                            return entry.getValue().stream()
                                    .flatMap(idValue -> {
                                        Map<String, Object> filter = Map.of("id", idValue);

                                        return catalogSearch.findByCriteria(filter, docType.getCatalogEntity())
                                                .stream()
                                                .map(entity -> {
                                                    DocElement doc = editorMapper.fromCatalog(specId, entity, null, docType);

                                                    Map<String, Object> updateMap = mongoUpdateBuilder.toMap(doc);
                                                    Update update = new Update();
                                                    updateMap.forEach((key, value) -> {
                                                        if ("_class".equals(key)) update.set(key, value);
                                                    });

                                                    ObjectId persistedId = documentSearch.upsert(doc, docType.getDocElement());
                                                    doc.setId(persistedId);

                                                    dtoList.stream()
                                                            .filter(dto -> dto.elementId().equals(doc.getCatalogId()))
                                                            .findFirst()
                                                            .ifPresent(dto -> handleElementAssociation(dto, doc, specId));

                                                    return doc;
                                                });
                                    })
                                    .collect(Collectors.toList());
                        }
                ));

        return sortedDocs.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(doc -> ResDocElementDTO.fromDoc(doc, entry.getKey().getResDocSupplier()))
                                .toList()
                ));
    }

    private void handleElementAssociation(DocElementCatalogCreationDTO dto, DocElement doc, ObjectId specId) {
        if ((dto.type().equals(DocElementEnum.ITEM) || dto.type().equals(DocElementEnum.MARCA))
                && dto.parentId() != null) {

            doc.setParentId(dto.parentId());
            associateIfNeeded(dto, doc);
            return;
        }

        EspecificacaoDoc especificacaoDoc = getSpecById(specId);

        if (doc instanceof AmbienteDocElement ambiente) {
            LocalDoc local = localDocRepository.findByLocalAndEspecificacaoId(ambiente.getLocal(), specId)
                    .orElseThrow(() -> new ResourceNotFoundException("Local não encontrado: " + ambiente.getLocal()));

            local.getAmbienteIds().add(ambiente.getId());
            localDocRepository.save(local);

        } else if (doc instanceof MaterialDocElement material) {
            especificacaoDoc.getMateriaisIds().add(material.getId());
            especificacaoDocRepository.save(especificacaoDoc);
        }
    }

    private boolean isHexString(String string) {
        return string.matches("^[0-9A-Fa-f]+$");
    }

    public ResDocElementDTO createRawElement(ObjectId specId, DocElementDTO dto) {
        EspecificacaoDoc spec = getSpecById(specId);

        DocElement newElement = docElementBuilder.create(specId, dto);
        saveElement(spec, newElement, dto.getDocType());

        return ResDocElementDTO.fromDoc(newElement, dto.getDocType().getResDocSupplier());
    }

    private void saveElement(EspecificacaoDoc spec, DocElement element, DocElementEnum type) {
        switch (type) {
            case AMBIENTE -> saveAmbiente(spec, (AmbienteDocElement) element);
            case ITEM -> saveItem((ItemDocElement) element);
            case MATERIAL -> saveMaterial(spec, (MaterialDocElement) element);
            case MARCA -> saveMarca((MarcaDocElement) element);
            default -> throw new InvalidDocumentTypeException("Tipo de elemento não suportado: " + type);
        }
    }

    private void saveAmbiente(EspecificacaoDoc spec, AmbienteDocElement ambiente) {
        checkDuplicateAmbiente(ambiente);
        LocalDoc local = localDocRepository.findByLocalAndEspecificacaoId(ambiente.getLocal(), spec.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Local '" + ambiente.getLocal() + "' não encontrado"));

        ambiente.setParentId(local.getId());
        ambienteDocRepository.save(ambiente);

        local.getAmbienteIds().add(ambiente.getId());
        localDocRepository.save(local);
    }

    private void saveItem(ItemDocElement item) {
        if (item.getParentId() != null) {
            AmbienteDocElement ambiente = ambienteDocRepository.findById(item.getParentId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Ambiente não encontrado: " + item.getParentId())
                    );
            associateItemToAmbiente(ambiente, item);
        } else {
            itemDocRepository.save(item);
        }
    }

    private void saveMaterial(EspecificacaoDoc spec, MaterialDocElement material) {
        checkDuplicateMaterial(material);
        materialDocRepository.save(material);

        spec.getMateriaisIds().add(material.getId());
        especificacaoDocRepository.save(spec);
    }

    private void saveMarca(MarcaDocElement marca) {
        if (marca.getParentId() != null) {
            MaterialDocElement material = materialDocRepository.findById(marca.getParentId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Material não encontrado: " + marca.getParentId())
                    );
            associateMarcaToMaterial(material, marca);
        } else {
            marcaDocRepository.save(marca);
        }
    }

    private void associateIfNeeded(DocElementCatalogCreationDTO dto, DocElement doc) {
        DocElementEnum parentType = switch (dto.type()) {
            case ITEM -> DocElementEnum.AMBIENTE;
            case MARCA -> DocElementEnum.MATERIAL;
            default -> throw new UnsupportedOperationException(
                    "Associação não suportada para tipo: " + dto.type()
            );
        };

        ObjectId parentId = dto.parentId();
        DocElement parentDoc = documentSearch.findInDocument(parentId, parentType.getDocElement());
        if (parentDoc == null) {
            throw new ResourceNotFoundException("Elemento associado não encontrado: " + dto.parentId());
        }

        switch (parentDoc) {
            case AmbienteDocElement ambiente when doc instanceof ItemDocElement item -> {
                if (ambiente.getItemIds().contains(item.getId())) {
                    throw new AssociationAlreadyExistsException("O item já está associado ao ambiente");
                }
                ambiente.getItemIds().add(item.getId());
                ambienteDocRepository.save(ambiente);
            }
            case MaterialDocElement material when doc instanceof MarcaDocElement marca -> {
                if (material.getMarcaIds().contains(marca.getId())) {
                    throw new AssociationAlreadyExistsException("A marca já está associada ao material");
                }
                material.getMarcaIds().add(marca.getId());
                materialDocRepository.save(material);
            }
            default -> throw new UnsupportedOperationException("Tipo de associação inválido: " + dto.type());
        }
    }

    private void associateItemToAmbiente(AmbienteDocElement ambiente, ItemDocElement item) {
        checkDuplicateItem(item);
        itemDocRepository.save(item);

        ambiente.getItemIds().add(item.getId());
        ambienteDocRepository.save(ambiente);
    }

    private void associateMarcaToMaterial(MaterialDocElement material, MarcaDocElement marca) {
        checkDuplicateMarca(marca);
        marcaDocRepository.save(marca);

        material.getMarcaIds().add(marca.getId());
        materialDocRepository.save(material);
    }

    private void checkDuplicateAmbiente(AmbienteDocElement ambiente) {
        boolean exists = ambienteDocRepository.existsByNameAndParentId(
                ambiente.getName(),
                ambiente.getParentId()
        );

        if (exists) {
            throw new ResourceAlreadyExistsException("Ambiente duplicado: " + ambiente.getName());
        }
    }

    private void checkDuplicateItem(ItemDocElement item) {
        boolean exists = itemDocRepository.existsByNameAndDescAndTypeAndParentId(
                item.getName(),
                item.getDesc(),
                item.getType(),
                item.getParentId()
        );

        if (exists) {
            throw new ResourceAlreadyExistsException("Item duplicado: " + item.getName());
        }
    }

    private void checkDuplicateMaterial(MaterialDocElement material) {
        boolean exists = materialDocRepository.existsByNameAndEspecificacaoId(
                material.getName(),
                material.getEspecificacaoId()
        );

        if (exists) {
            throw new ResourceAlreadyExistsException("Material duplicado: " + material.getName());
        }
    }

    private void checkDuplicateMarca(MarcaDocElement marca) {
        boolean exists = marcaDocRepository.existsByNameAndParentId(marca.getName(), marca.getParentId());

        if (exists) {
            throw new ResourceAlreadyExistsException("Marca duplicada: " + marca.getName());
        }
    }

    public ResDocElementDTO update(ObjectId id, DocElementDTO dto) {
        return switch (dto.getDocType()) {
            case AMBIENTE -> updateAmbiente(id, (AmbienteDocDTO) dto);
            case ITEM -> updateItem(id, (ItemDocDTO) dto);
            case MATERIAL -> updateMaterial(id, (MaterialElementDTO) dto);
            case MARCA -> updateMarca(id, (MarcaElementDTO) dto);
        };
    }

    private ResAmbDocDTO updateAmbiente(ObjectId id, AmbienteDocDTO dto) {
        AmbienteDocElement ambiente = ambienteDocRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ambiente não encontrado com o id informado: " + id)
                );

        if (dto.getName() != null && !dto.getName().isBlank()) {
            ambiente.setName(dto.getName());
        }

        if (dto.getLocal() != null) {
            ambiente.setLocal(dto.getLocal());

            if (dto.getParentId() != null) {
                LocalDoc local = localDocRepository.findById(dto.getParentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Local não encontrado com o id informado: " + dto.getParentId())
                        );

                if (ambiente.getParentId() == null) {
                    ambiente.setParentId(local.getId());
                } else {
                    LocalDoc prevLocal = localDocRepository.findById(ambiente.getParentId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Local anterior não encontrado: " + ambiente.getParentId())
                            );
                    ambiente.setParentId(local.getId());
                    prevLocal.getAmbienteIds().remove(ambiente.getId());
                }

                checkDuplicateAmbiente(ambiente);
                local.getAmbienteIds().add(ambiente.getId());
                localDocRepository.save(local);
            } else {
                LocalDoc local = localDocRepository.findByLocalAndEspecificacaoId(ambiente.getLocal(), ambiente.getEspecificacaoId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Local anterior não encontrado: " + ambiente.getParentId())
                        );

                checkDuplicateAmbiente(ambiente);
                ambiente.setParentId(local.getId());
                local.getAmbienteIds().add(ambiente.getId());
            }
        }

        ambiente.setInSync(syncService.inSync(ambiente, DocElementEnum.AMBIENTE));
        return ResAmbDocDTO.fromDoc(ambienteDocRepository.save(ambiente));
    }

    private ResItemDocDTO updateItem(ObjectId id, ItemDocDTO dto) {
        ItemDocElement item = itemDocRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Item não encontrado com o id informado: " + id)
                );

        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }

        if (dto.getParentId() != null) {
            AmbienteDocElement newAmbiente = ambienteDocRepository.findById(dto.getParentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Ambiente não encontrado com o id informado: " + dto.getParentId())
                    );

            if (item.getParentId() != null && !item.getParentId().equals(dto.getParentId())) {
                AmbienteDocElement prevAmbiente = ambienteDocRepository.findById(item.getParentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Ambiente anterior não encontrado: " + item.getParentId())
                        );
                prevAmbiente.getItemIds().remove(item.getId());
                ambienteDocRepository.save(prevAmbiente);
            }

            checkDuplicateItem(item);
            item.setParentId(newAmbiente.getId());
            newAmbiente.getItemIds().add(item.getId());
            ambienteDocRepository.save(newAmbiente);
        }

        item.setInSync(syncService.inSync(item, DocElementEnum.ITEM));
        return ResItemDocDTO.fromDoc(itemDocRepository.save(item));
    }

    private ResMatDocDTO updateMaterial(ObjectId id, MaterialElementDTO dto) {
        MaterialDocElement material = materialDocRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Material não encontrado com o id informado: " + id)
                );

        if (dto.getName() != null && !dto.getName().isBlank()) {
            material.setName(dto.getName());
        }

        ObjectId spec = material.getEspecificacaoId();
        if (spec == null) {
            throw new ResourceNotFoundException("Especificação não associada ao material: " + id);
        }

        checkDuplicateMaterial(material);

        material.setInSync(syncService.inSync(material, DocElementEnum.MATERIAL));
        return ResMatDocDTO.fromDoc(materialDocRepository.save(material));
    }

    private ResMarDocDTO updateMarca(ObjectId id, MarcaElementDTO dto) {
        MarcaDocElement marca = marcaDocRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Marca não encontrada com o id informado: " + id)
                );

        if (dto.getName() != null && !dto.getName().isBlank()) {
            marca.setName(dto.getName());
        }

        if (dto.getParentId() != null) {
            MaterialDocElement newMaterial = materialDocRepository.findById(dto.getParentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Material não encontrado com o id informado: " + dto.getParentId())
                    );

            if (marca.getParentId() != null && !marca.getParentId().equals(dto.getParentId())) {
                MaterialDocElement oldMaterial = materialDocRepository.findById(marca.getParentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Material anterior não encontrado: " + marca.getParentId())
                        );
                oldMaterial.getMarcaIds().remove(marca.getId());
                materialDocRepository.save(oldMaterial);
            }

            checkDuplicateMarca(marca);
            marca.setParentId(newMaterial.getId());
            newMaterial.getMarcaIds().add(marca.getId());
            materialDocRepository.save(newMaterial);
        }

        marca.setInSync(syncService.inSync(marca, DocElementEnum.MARCA));
        return ResMarDocDTO.fromDoc(marcaDocRepository.save(marca));
    }

    public void delete(ObjectId id, DocElementEnum type) {
        documentSearch.deleteWithReferences(id, type.getDocElement());
    }
}
