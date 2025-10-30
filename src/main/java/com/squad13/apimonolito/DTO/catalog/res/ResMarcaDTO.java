package com.squad13.apimonolito.DTO.catalog.res;

import java.util.Set;

public record ResMarcaDTO(
        Long id,
        String name,
        Boolean isActive,

        Set<ResMinDTO> materialSet,
        Set<ResMinDTO> padraoSet
) {
}