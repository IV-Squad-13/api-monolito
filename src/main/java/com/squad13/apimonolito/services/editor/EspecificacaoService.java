package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.DTO.editor.EspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.EspecificacaoSearchParamsDTO;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.edit.EditEspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.res.ResSpecDTO;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.services.catalog.ComposicaoService;
import com.squad13.apimonolito.util.builder.DocElementBuilder;
import com.squad13.apimonolito.util.enums.LocalEnum;
import com.squad13.apimonolito.util.mapper.CatalogMapper;
import com.squad13.apimonolito.util.mapper.EditorMapper;
import com.squad13.apimonolito.util.search.DocumentSearch;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EspecificacaoService {

    private final EmpreendimentoRepository empRepository;
    private final ComposicaoService compService;

    private final EspecificacaoDocRepository specRepository;

    private final EditorMapper editorMapper;
    private final CatalogMapper catalogMapper;

    private final DocElementBuilder docBuilder;

    private final DocumentSearch documentSearch;

    public List<ResSpecDTO> findAll(LoadDocumentParamsDTO params) {
        Aggregation aggregation = docBuilder.buildAggregation(params);
        return documentSearch.findWithAggregation("especificacoes", ResSpecDTO.class, aggregation);
    }

    // TODO: Implementar busca por id com agregação
    public ResSpecDTO findById(ObjectId id, LoadDocumentParamsDTO params) {
        Aggregation aggregation = docBuilder.buildAggregation(params);
        return documentSearch.findWithAggregation("especificacoes", ResSpecDTO.class, aggregation)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o ID: " + id));
    }

    private EspecificacaoDoc findById(ObjectId id) {
        return specRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o ID: " + id));
    }

    public List<ResSpecDTO> search(LoadDocumentParamsDTO loadParams, EspecificacaoSearchParamsDTO searchParams) {
        Map<String, Object> filters = EspecificacaoSearchParamsDTO.buildFilters(searchParams);
        List<MatchOperation> matchOperations = documentSearch.buildMatchOps(filters);

        Aggregation agg = docBuilder.buildAggregation(loadParams);
        return documentSearch.searchWithAggregation(
                "especificacoes",
                ResSpecDTO.class,
                matchOperations,
                agg
        );
    }

    public ResSpecDTO findByEmpId(Long id, LoadDocumentParamsDTO params) {
        return search(params, new EspecificacaoSearchParamsDTO(id)).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o Empreendimento: " + id));
    }

    @Transactional
    public ResSpecDTO create(EspecificacaoDocDTO dto) {
        Empreendimento emp = empRepository.findById(dto.empId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Empreendimento não encontrado para o ID: " + dto.empId())
                );

        return switch (dto.initType()) {
            case AVULSO -> createEspecificacaoAvulso(dto);
            case PADRAO -> createFromPadrao(dto, emp);
            case IMPORT -> createFromImport(dto); // TODO: implementar
        };
    }

    private ResSpecDTO createEspecificacaoAvulso(EspecificacaoDocDTO dto) {
        EspecificacaoDoc spec = new EspecificacaoDoc();
        spec.setName(dto.name());
        spec.setEmpreendimentoId(dto.empId());
        spec.setDesc(dto.desc());
        spec.setObs(dto.obs());

        spec.setId(newId());

        List<LocalDoc> locais = generateLocais(spec.getId());
        documentSearch.bulkSave(LocalDoc.class, locais);

        spec.setLocaisIds(
                locais.stream().map(LocalDoc::getId).collect(Collectors.toList())
        );

        return editorMapper.toResponse(specRepository.save(spec));
    }

    private List<LocalDoc> generateLocais(ObjectId specId) {
        return Arrays.stream(LocalEnum.values())
                .map(localEnum -> {
                    LocalDoc local = new LocalDoc();
                    local.setId(newId());
                    local.setEspecificacaoId(specId);
                    local.setLocal(localEnum);
                    return local;
                })
                .toList();
    }

    private ResSpecDTO createFromPadrao(EspecificacaoDocDTO dto, Empreendimento emp) {
        EspecificacaoDoc spec = new EspecificacaoDoc();
        spec.setId(newId());
        spec.setName(dto.name());
        spec.setEmpreendimentoId(dto.empId());
        spec.setDesc(dto.desc());
        spec.setObs(dto.obs());

        List<LocalDoc> locais = generateLocais(spec.getId());
        spec.setLocaisIds(locais.stream().map(LocalDoc::getId).toList());

        List<ItemAmbiente> itemAmbientes = compService.findItensAmbienteByPadrao(emp.getPadrao().getId());
        Map<Ambiente, List<ItemDesc>> groupedAmbientes =
                catalogMapper.groupBy(itemAmbientes, ItemAmbiente::getAmbiente, ItemAmbiente::getItemDesc);

        List<AmbienteDocElement> ambientesToSave = new ArrayList<>();
        List<ItemDocElement> itemsToSave = new ArrayList<>();

        groupedAmbientes.forEach((catalogAmb, catalogItems) -> {
            AmbienteDocElement ambiente = editorMapper.fromCatalog(spec.getId(), catalogAmb);
            ambiente.setId(newId());

            locais.stream()
                    .filter(l -> l.getLocal().equals(ambiente.getLocal()))
                    .findFirst()
                    .ifPresent(l -> ambiente.setParentId(l.getId()));

            catalogItems.forEach(catalogItem -> {
                ItemDocElement item = editorMapper.fromCatalog(spec.getId(), catalogItem);
                item.setId(newId());
                item.setParentId(ambiente.getId());
                itemsToSave.add(item);
            });

            ambientesToSave.add(ambiente);
        });

        documentSearch.bulkSave(ItemDocElement.class, itemsToSave);

        Map<ObjectId, List<ObjectId>> itemIdsByAmbiente = itemsToSave.stream()
                .collect(Collectors.groupingBy(ItemDocElement::getParentId,
                        Collectors.mapping(ItemDocElement::getId, Collectors.toList())));

        ambientesToSave.forEach(ambiente -> {
            List<ObjectId> itemIds = itemIdsByAmbiente.getOrDefault(ambiente.getId(), Collections.emptyList());
            ambiente.setItemIds(itemIds);
        });

        documentSearch.bulkSave(AmbienteDocElement.class, ambientesToSave);

        ambientesToSave.forEach(amb -> locais.stream()
                .filter(l -> l.getLocal().equals(amb.getLocal()))
                .forEach(l -> l.getAmbienteIds().add(amb.getId())));

        documentSearch.bulkSave(LocalDoc.class, locais);

        List<MarcaMaterial> marcaMateriais = compService.findMarcasMaterialByPadrao(emp.getPadrao().getId());
        Map<Material, List<Marca>> groupedMateriais =
                catalogMapper.groupBy(marcaMateriais, MarcaMaterial::getMaterial, MarcaMaterial::getMarca);

        List<MaterialDocElement> materialsToSave = new ArrayList<>();
        List<MarcaDocElement> marcasToSave = new ArrayList<>();

        groupedMateriais.forEach((catalogMaterial, catalogMarcas) -> {
            MaterialDocElement material = editorMapper.fromCatalog(spec.getId(), catalogMaterial);
            material.setId(newId());

            catalogMarcas.forEach(catalogMarca -> {
                MarcaDocElement marca = editorMapper.fromCatalog(spec.getId(), catalogMarca);
                marca.setId(newId());
                marca.setParentId(material.getId());
                marcasToSave.add(marca);
                material.getMarcaIds().add(marca.getId());
            });

            materialsToSave.add(material);
        });

        documentSearch.bulkSave(MarcaDocElement.class, marcasToSave);
        documentSearch.bulkSave(MaterialDocElement.class, materialsToSave);

        spec.setMateriaisIds(materialsToSave.stream().map(MaterialDocElement::getId).toList());

        documentSearch.bulkSave(EspecificacaoDoc.class, List.of(spec));

        return editorMapper.toResponse(spec);
    }

    private static ObjectId newId() {
        return new ObjectId();
    }

    private ResSpecDTO createFromImport(EspecificacaoDocDTO dto) {
        // TODO: implementar inicialização por importação
        throw new UnsupportedOperationException("Inicialização por IMPORT ainda não implementada");
    }

    public ResSpecDTO update(ObjectId id, EditEspecificacaoDocDTO dto) {
        EspecificacaoDoc especificacacaoDoc = findById(id);

        if (dto.name() != null && !dto.name().isEmpty()) {
            especificacacaoDoc.setName(dto.name());
        }

        if (dto.desc() != null) {
            especificacacaoDoc.setDesc(dto.desc());
        }

        if (dto.obs() != null) {
            especificacacaoDoc.setObs(dto.obs());
        }

        if (dto.empId() != null) {
            especificacacaoDoc.setEmpreendimentoId(dto.empId());
        }

        return editorMapper.toResponse(specRepository.save(especificacacaoDoc));
    }

    public void delete(ObjectId id) {
        documentSearch.deleteWithReferences(id, EspecificacaoDoc.class);
    }

    public void delete(Long empId) {
        ResSpecDTO spec = findByEmpId(empId, LoadDocumentParamsDTO.allFalse());
        delete(new ObjectId(spec.getId()));
    }
}