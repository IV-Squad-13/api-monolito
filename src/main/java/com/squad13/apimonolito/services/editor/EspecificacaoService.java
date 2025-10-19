package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.DTO.catalog.res.*;
import com.squad13.apimonolito.DTO.editor.EspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.edit.EditEspecificacaoDocDTO;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.mongo.editor.*;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.services.catalog.ComposicaoService;
import com.squad13.apimonolito.util.enums.LocalEnum;
import com.squad13.apimonolito.util.mappers.EditorMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EspecificacaoService {

    private final EmpreendimentoRepository empRepository;
    private final ComposicaoService compService;

    private final EspecificacaoDocRepository specRepository;

    private final LocalDocRepository localDocRepository;
    private final AmbienteDocElementRepository ambienteDocRepository;
    private final ItemDocElementRepository itemDocRepository;
    private final MaterialDocElementRepository materialDocRepository;
    private final MarcaDocElementRepository marcaDocRepository;
    private final EditorMapper editorMapper;

    public List<EspecificacaoDoc> getAll() {
        return specRepository.findAll();
    }

    public EspecificacaoDoc getById(String id) {
        return specRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o ID: " + id));
    }

    @Transactional
    public EspecificacaoDoc create(EspecificacaoDocDTO dto) {
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

    private String generateSpecId() {
        return ObjectId.get().toHexString();
    }

    private EspecificacaoDoc createEspecificacaoAvulso(EspecificacaoDocDTO dto) {
        EspecificacaoDoc spec = new EspecificacaoDoc();
        spec.setName(dto.name());
        spec.setEmpreendimentoId(dto.empId());
        spec.setDesc(dto.desc());
        spec.setObs(dto.obs());

        spec.setId(generateSpecId());

        List<LocalDoc> locais = generateLocais(spec.getId());
        localDocRepository.saveAll(locais);

        spec.setLocaisIds(
                locais.stream().map(LocalDoc::getId).collect(Collectors.toList())
        );

        return specRepository.save(spec);
    }

    private List<LocalDoc> generateLocais(String specId) {
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

    public EspecificacaoDoc createFromPadrao(EspecificacaoDocDTO dto, Empreendimento emp) {
        EspecificacaoDoc spec = new EspecificacaoDoc();
        spec.setId(newId());
        spec.setName(dto.name());
        spec.setEmpreendimentoId(dto.empId());
        spec.setDesc(dto.desc());
        spec.setObs(dto.obs());

        List<LocalDoc> locais = generateLocais(spec.getId());
        spec.setLocaisIds(
                locais.stream().map(LocalDoc::getId).collect(Collectors.toList())
        );

        List<ItemAmbiente> itemAmbientes = compService.findItensAmbienteByPadrao(emp.getPadrao().getId());
        Map<Ambiente, List<ItemDesc>> groupedAmbientes = groupBy(itemAmbientes, ItemAmbiente::getAmbiente, ItemAmbiente::getItemDesc);

        List<AmbienteDocElement> ambientesToSave = new ArrayList<>();
        List<ItemDocElement> itemsToSave = new ArrayList<>();

        groupedAmbientes.forEach((catalogAmb, catalogItems) -> {
            AmbienteDocElement ambiente = editorMapper.fromCatalog(spec.getId(), catalogAmb);
            String ambienteId = newId();
            ambiente.setId(ambienteId);

            locais.stream()
                    .filter(l -> l.getLocal().equals(ambiente.getLocal()))
                    .map(LocalDoc::getId)
                    .forEach(ambiente::setId);

            catalogItems.forEach(catalogItem -> {
                ItemDocElement item = editorMapper.fromCatalog(spec.getId(), catalogItem);
                item.setId(newId());
                item.setParentId(ambienteId);
                itemsToSave.add(item);
            });

            ambientesToSave.add(ambiente);
        });

        List<ItemDocElement> savedItems = itemDocRepository.saveAll(itemsToSave);
        Map<String, List<String>> itemIdsByAmbiente =
                savedItems.stream()
                        .collect(Collectors.groupingBy(ItemDocElement::getParentId,
                                Collectors.mapping(ItemDocElement::getId, Collectors.toList())));

        ambientesToSave.forEach(ambiente -> {
            List<String> itemIds = itemIdsByAmbiente.getOrDefault(ambiente.getId(), Collections.emptyList());
            ambiente.setItemIds(itemIds);
        });

        List<AmbienteDocElement> savedAmbientes = ambienteDocRepository.saveAll(ambientesToSave);

        savedAmbientes.forEach(ambiente -> {
            locais.forEach(l -> {
                if (l.getId().equals(ambiente.getId())) {
                    l.getAmbienteIds().add(ambiente.getId());
                }
            });
        });

        localDocRepository.saveAll(locais);

        List<MarcaMaterial> marcaMateriais = compService.findMarcasMaterialByPadrao(emp.getPadrao().getId());
        Map<Material, List<Marca>> groupedMateriais = groupBy(marcaMateriais, MarcaMaterial::getMaterial, MarcaMaterial::getMarca);

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

        marcaDocRepository.saveAll(marcasToSave);
        List<MaterialDocElement> savedMaterials = materialDocRepository.saveAll(materialsToSave);

        spec.setMateriaisIds(savedMaterials.stream().map(MaterialDocElement::getId).toList());

        return specRepository.save(spec);
    }

    private static String newId() {
        return new ObjectId().toHexString();
    }

    private <T, K, V> Map<K, List<V>> groupBy(
            List<T> items,
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper
    ) {
        return items.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        keyMapper,
                        Collectors.mapping(valueMapper, Collectors.toList())
                ));
    }

    private EspecificacaoDoc createFromImport(EspecificacaoDocDTO dto) {
        // TODO: implementar inicialização por importação
        throw new UnsupportedOperationException("Inicialização por IMPORT ainda não implementada");
    }

    public EspecificacaoDoc update(String id, EditEspecificacaoDocDTO dto) {
        EspecificacaoDoc especificacacaoDoc = getById(id);

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

        return specRepository.save(especificacacaoDoc);
    }

    public void delete(String id) {
        itemDocRepository.deleteAllByEspecificacaoId(id);
        ambienteDocRepository.deleteAllByEspecificacaoId(id);
        localDocRepository.deleteAllByEspecificacaoId(id);
        marcaDocRepository.deleteAllByEspecificacaoId(id);
        materialDocRepository.deleteAllByEspecificacaoId(id);

        specRepository.deleteById(id);
    }
}