package com.squad13.apimonolito.util.mapper;

import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.res.*;
import com.squad13.apimonolito.exceptions.InvalidCompositorException;
import com.squad13.apimonolito.models.catalog.*;
import com.squad13.apimonolito.models.catalog.associative.ComposicaoAmbiente;
import com.squad13.apimonolito.models.catalog.associative.ComposicaoMaterial;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CatalogMapper {

    public ResItemDTO toResponse(ItemDesc item, LoadCatalogParamsDTO loadDTO) {
        if (item == null) return null;

        ResItemTypeDTO typeDto = toResItemTypeDTO(item);

        Set<ResMinDTO> padroes = loadDTO.isLoadPadroes() ? getAmbienteMinPadraoDTO(item.getAmbienteSet()) : Collections.emptySet();
        Set<ResMinDTO> ambientes = loadDTO.isLoadAmbientes() ? getMinAmbienteDTO(item.getAmbienteSet()) : Collections.emptySet();

        return new ResItemDTO(
                item.getId(),
                item.getName(),
                item.getIsActive(),
                item.getDesc(),
                typeDto,
                ambientes,
                padroes
        );
    }

    public ResItemTypeDTO toResponse(ItemType item) {
        if (item == null) return null;

        Set<ResMinDTO> items = getMinItemDTOByEntity(item.getItemDescSet());

        return new ResItemTypeDTO(
                item.getId(),
                item.getName(),
                item.getIsActive(),
                items
        );
    }

    public ResAmbienteDTO toResponse(Ambiente ambiente, LoadCatalogParamsDTO params) {
        if (ambiente == null) return null;

        Set<ResMinDTO> padroes = params.isLoadPadroes() ? getAmbienteMinPadraoDTO(ambiente.getItemSet()) : Collections.emptySet();
        Set<ResMinDTO> items = params.isLoadItems() ? getMinItemDTO(ambiente.getItemSet()) : Collections.emptySet();

        return new ResAmbienteDTO(
                ambiente.getId(),
                ambiente.getName(),
                ambiente.getLocal(),
                ambiente.getIsActive(),
                items,
                padroes
        );
    }

    public ResItemAmbienteDTO toResponse(ItemAmbiente association) {
        if (association == null) return null;

        return new ResItemAmbienteDTO(
                association.getId(),
                toResponse(association.getItemDesc(), LoadCatalogParamsDTO.allFalse()),
                toResponse(association.getAmbiente(), LoadCatalogParamsDTO.allFalse())
        );
    }

    public ResMarcaDTO toResponse(Marca marca, LoadCatalogParamsDTO loadDTO) {
        if (marca == null) return null;

        Set<ResMinDTO> padroes = loadDTO.isLoadPadroes() ? getMaterialMinPadraoDTO(marca.getMaterialSet()) : Collections.emptySet();
        Set<ResMinDTO> materiais = loadDTO.isLoadMateriais() ? getMinMaterialDTO(marca.getMaterialSet()) : Collections.emptySet();

        return new ResMarcaDTO(
                marca.getId(),
                marca.getName(),
                marca.getIsActive(),
                materiais,
                padroes
        );
    }

    public ResMaterialDTO toResponse(Material material, LoadCatalogParamsDTO loadDTO) {
        if (material == null) return null;

        Set<ResMinDTO> padroes = loadDTO.isLoadPadroes() ? getMaterialMinPadraoDTO(material.getMarcaSet()) : Collections.emptySet();
        Set<ResMinDTO> marcas = loadDTO.isLoadMarcas() ? getMinMarcaDTO(material.getMarcaSet()) : Collections.emptySet();

        return new ResMaterialDTO(
                material.getId(),
                material.getName(),
                material.getIsActive(),
                marcas,
                padroes
        );
    }

    public ResMarcaMaterialDTO toResponse(MarcaMaterial association) {
        if (association == null) return null;

        return new ResMarcaMaterialDTO(
                association.getId(),
                toResponse(association.getMarca(), LoadCatalogParamsDTO.allFalse()),
                toResponse(association.getMaterial(), LoadCatalogParamsDTO.allFalse())
        );
    }

    public ResPadraoDTO toResponse(Padrao padrao, LoadCatalogParamsDTO params) {
        if (padrao == null) return null;

        List<ResMinDTO> items = params.isLoadItems()
                ? padrao.getAmbienteSet().stream()
                    .map(comp -> toMinDTO(comp.getCompositor().getItemDesc(), params.isLoadNested()))
                    .toList()
                : List.of();

        List<ResMinDTO> ambientes = params.isLoadAmbientes()
                ? padrao.getAmbienteSet().stream()
                    .map(comp -> toMinDTO(comp.getCompositor().getAmbiente(), params.isLoadNested()))
                    .toList()
                : List.of();

        List<ResMinDTO> marcas = params.isLoadMarcas()
                ? padrao.getMaterialSet().stream()
                    .map(comp -> toMinDTO(comp.getCompositor().getMarca(), params.isLoadNested()))
                    .toList()
                : List.of();

        List<ResMinDTO> materiais = params.isLoadMateriais()
                ? padrao.getMaterialSet().stream()
                    .map(comp -> toMinDTO(comp.getCompositor().getMaterial(), params.isLoadNested()))
                    .toList()
                : List.of();

        return new ResPadraoDTO(
                padrao.getId(),
                padrao.getName(),
                padrao.getIsActive(),
                ambientes,
                items,
                materiais,
                marcas
        );
    }

    private ResItemTypeDTO toResItemTypeDTO(ItemDesc item) {
        return Optional.ofNullable(item.getType())
                .map(type -> new ResItemTypeDTO(
                        type.getId(),
                        type.getName(),
                        type.getIsActive(),
                        Set.of()
                ))
                .orElse(null);
    }

    private Set<ResMinDTO> getMinAmbienteDTO(Set<ItemAmbiente> itemAmbienteSet) {
        return itemAmbienteSet.stream()
                .map(rel -> toMinDTO(rel.getAmbiente(), false))
                .collect(Collectors.toSet());
    }

    private Set<ResMinDTO> getMinItemDTO(Set<ItemAmbiente> itemAmbienteSet) {
        return itemAmbienteSet.stream()
                .map(rel -> toMinDTO(rel.getItemDesc(), false))
                .collect(Collectors.toSet());
    }

    private Set<ResMinDTO> getMinMaterialDTO(Set<MarcaMaterial> marcaMaterialSet) {
        return marcaMaterialSet.stream()
                .map(rel -> toMinDTO(rel.getMaterial(), false))
                .collect(Collectors.toSet());
    }

    private Set<ResMinDTO> getMinMarcaDTO(Set<MarcaMaterial> marcaMaterialSet) {
        return marcaMaterialSet.stream()
                .map(rel -> toMinDTO(rel.getMarca(), false))
                .collect(Collectors.toSet());
    }

    private Set<ResMinDTO> getMinItemDTOByEntity(Set<ItemDesc> itemDescSet) {
        return itemDescSet.stream()
                .map(it -> toMinDTO(it, false))
                .collect(Collectors.toSet());
    }

    private Set<ResMinDTO> getAmbienteMinPadraoDTO(Set<ItemAmbiente> relSet) {
        return relSet.stream()
                .flatMap(rel -> rel.getCompSet().stream()
                        .map(comp -> toMinDTO(comp.getPadrao(), false))
                ).collect(Collectors.toSet());
    }

    private Set<ResMinDTO> getMaterialMinPadraoDTO(Set<MarcaMaterial> relSet) {
        return relSet.stream()
                .flatMap(rel -> rel.getCompSet().stream()
                        .map(comp -> toMinDTO(comp.getPadrao(), false))
                ).collect(Collectors.toSet());
    }

    private ResMinDTO toMinDTO(ItemDesc it, boolean loadAmbientes) {
        List<Long> associatedIds = loadAmbientes
                ? it.getAmbienteSet().stream().map(ia -> ia.getAmbiente().getId()).toList()
                : List.of();

        return new ResMinDTO(it.getId(), it.getDesc(), it.getIsActive(), associatedIds);
    }

    private ResMinDTO toMinDTO(Ambiente am, boolean loadItems) {
        List<Long> associatedIds = loadItems
                ? am.getItemSet().stream().map(ia -> ia.getAmbiente().getId()).toList()
                : List.of();

        return new ResMinDTO(am.getId(), am.getName(), am.getIsActive(), associatedIds);
    }

    private ResMinDTO toMinDTO(Marca mr, boolean loadMateriais) {
        List<Long> associatedIds = loadMateriais
                ? mr.getMaterialSet().stream().map(mm -> mm.getMaterial().getId()).toList()
                : List.of();

        return new ResMinDTO(mr.getId(), mr.getName(), mr.getIsActive(), associatedIds);
    }

    private ResMinDTO toMinDTO(Material mt, boolean loadMarcas) {
        List<Long> associatedIds = loadMarcas
                ? mt.getMarcaSet().stream().map(mm -> mm.getMarca().getId()).toList()
                : List.of();

        return new ResMinDTO(mt.getId(), mt.getName(), mt.getIsActive(), associatedIds);
    }

    private ResMinDTO toMinDTO(Padrao p, boolean loadEmps) {
        List<Long> associatedIds = loadEmps
                ? p.getEmpreendimentoSet().stream().map(Empreendimento::getId).toList()
                : List.of();

        return new ResMinDTO(p.getId(), p.getName(), p.getIsActive(), associatedIds);
    }

    public <T, K, V> Map<K, List<V>> groupBy(
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

    // COMPOSIÇÂO

    public ResComposicaoDTO toCompDTO(Object comp) {
        ResComposicaoDTO dto = new ResComposicaoDTO();

        if (comp instanceof ComposicaoAmbiente ca) {
            dto.setId(ca.getId());
            dto.setPadrao(toResponse(ca.getPadrao(), LoadCatalogParamsDTO.allFalse()));
            dto.setCompAmbiente(toResponse(ca.getCompositor()));
        } else if (comp instanceof ComposicaoMaterial cm) {
            dto.setId(cm.getId());
            dto.setPadrao(toResponse(cm.getPadrao(), LoadCatalogParamsDTO.allFalse()));
            dto.setCompMaterial(toResponse(cm.getCompositor()));
        } else {
            throw new InvalidCompositorException("Tipo inválido de composição: " + comp.getClass());
        }

        return dto;
    }
}