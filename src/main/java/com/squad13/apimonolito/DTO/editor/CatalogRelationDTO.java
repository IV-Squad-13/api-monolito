package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.CatalogRelationEnum;

public record CatalogRelationDTO(
        Long sourceId,
        Long targetId,
        CatalogRelationEnum type
) { }