package com.squad13.apimonolito.DTO.catalog.res;

import java.util.Set;

public record ResItemDTO(
        Long id,
        String name,
        Boolean isActive,
        String desc,

        ResItemTypeDTO type,
        Set<ResMinDTO> ambienteSet,
        Set<ResMinDTO> padraoSet
) { }