package com.squad13.apimonolito.util;

import com.squad13.apimonolito.DTO.catalog.res.*;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.associative.ComposicaoAmbiente;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Mapper {

    public ResItemDTO toResponse(ItemDesc item) {
        if (item == null) return null;

        ResItemTypeDTO typeDto = getResItemTypeDTO(item);

        Set<ItemAmbiente> itemAmbienteSet = item.getAmbienteSet();
        if (itemAmbienteSet == null) {
            itemAmbienteSet = Set.of();
        }

        Set<ResMinDTO> ambienteSet = getMinAmbienteDTO(itemAmbienteSet);

        Set<ResMinDTO> padraoSet = getAmbienteMinPadraoDTO(itemAmbienteSet);

        return new ResItemDTO(
                item.getId(),
                item.getName(),
                item.getIsActive(),
                item.getDesc(),
                typeDto,
                ambienteSet,
                padraoSet
        );
    }

    public ResAmbienteDTO toResponse(Ambiente ambiente) {
        if (ambiente == null) return null;

        Set<ItemAmbiente> itemAmbienteSet = ambiente.getItemSet();

        Set<ResMinDTO> itemSet = getMinItemDTO(itemAmbienteSet);

        Set<ResMinDTO> padraoSet = getAmbienteMinPadraoDTO(itemAmbienteSet);

        return new ResAmbienteDTO(
                ambiente.getId(),
                ambiente.getName(),
                ambiente.getLocal(),
                ambiente.getIsActive(),
                itemSet,
                padraoSet
        );
    }

    public ResItemAmbienteDTO toResponse(ItemAmbiente association) {
        if (association == null) return null;

        ResAmbienteDTO ambiente = toResponse(association.getAmbiente());
        ResItemDTO item = toResponse(association.getItemDesc());

        return new ResItemAmbienteDTO(
                association.getId(),
                item,
                ambiente
        );
    }

    private ResItemTypeDTO getResItemTypeDTO(ItemDesc item) {
        ResItemTypeDTO typeDto = null;
        if (item.getType() != null) {
            typeDto = new ResItemTypeDTO(
                    item.getType().getId(),
                    item.getType().getName(),
                    item.getType().getIsActive(),
                    Set.of(
                            new ResMinDTO(
                                    item.getId(),
                                    item.getName(),
                                    item.getIsActive()
                            )
                    )
            );
        }
        return typeDto;
    }

    private Set<ResMinDTO> getMinAmbienteDTO(Set<ItemAmbiente> itemAmbienteSet) {
        return itemAmbienteSet.stream()
                .map(rel -> new ResMinDTO(
                        rel.getAmbiente().getId(),
                        rel.getAmbiente().getName(),
                        rel.getAmbiente().getIsActive()
                ))
                .collect(Collectors.toSet());
    }

    private Set<ResMinDTO> getMinItemDTO(Set<ItemAmbiente> itemAmbienteSet) {
        return itemAmbienteSet.stream()
                .map(rel -> new ResMinDTO(
                        rel.getItemDesc().getId(),
                        rel.getItemDesc().getName(),
                        rel.getItemDesc().getIsActive()
                ))
                .collect(Collectors.toSet());
    }

    private Set<ResMinDTO> getAmbienteMinPadraoDTO(Set<ItemAmbiente> relSet) {
        return relSet.stream()
                .flatMap(rel -> rel.getCompSet().stream()
                        .map(comp -> new ResMinDTO(
                                comp.getPadrao().getId(),
                                comp.getPadrao().getName(),
                                comp.getPadrao().getIsActive()
                        ))
                )
                .collect(Collectors.toSet());
    }

    private Set<ResMinDTO> getMarcaMinPadraoDTO(Set<MarcaMaterial> relSet) {
        return relSet.stream()
                .flatMap(rel -> rel.getCompSet().stream()
                        .map(comp -> new ResMinDTO(
                                comp.getPadrao().getId(),
                                comp.getPadrao().getName(),
                                comp.getPadrao().getIsActive()
                        ))
                )
                .collect(Collectors.toSet());
    }
}
