package com.squad13.apimonolito.DTO.catalog.res;

import java.util.Set;

public record ResMaterialDTO(
        Long id,
        String name,
        Boolean isActive,

        Set<ResMinDTO> marcaSet,
        Set<ResMinDTO> padraoSet
) { }