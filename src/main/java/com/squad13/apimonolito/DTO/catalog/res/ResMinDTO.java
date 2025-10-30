package com.squad13.apimonolito.DTO.catalog.res;

import java.util.List;

public record ResMinDTO(
        Long id,
        String name,
        Boolean isActive,
        List<Long> associatedIds
) {
}