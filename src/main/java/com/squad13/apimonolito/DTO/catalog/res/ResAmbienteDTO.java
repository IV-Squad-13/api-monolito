package com.squad13.apimonolito.DTO.catalog.res;

import com.squad13.apimonolito.util.enums.LocalEnum;

import java.util.Set;

public record ResAmbienteDTO(
        Long id,
        String name,
        LocalEnum local,
        Boolean isActive,

        Set<ResMinDTO> itemSet,
        Set<ResMinDTO> padraoSet
) {
}