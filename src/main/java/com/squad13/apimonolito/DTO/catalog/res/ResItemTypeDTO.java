package com.squad13.apimonolito.DTO.catalog.res;

import java.util.Set;

public record ResItemTypeDTO(
        Long id,
        String name,
        Boolean isActive,

        Set<ResMinDTO> itemSet
) {
}