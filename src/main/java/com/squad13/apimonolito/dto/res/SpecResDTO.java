package com.squad13.apimonolito.dto.res;

import com.squad13.apimonolito.models.catalog.CatalogEntity;
import com.squad13.apimonolito.util.enums.CatalogSpecEnum;

import java.util.List;

public record SpecResDTO(
        Long id,
        String name,
        boolean isActive,
        CatalogSpecEnum level,
        List<SpecAssociationResDTO> associated
) { }