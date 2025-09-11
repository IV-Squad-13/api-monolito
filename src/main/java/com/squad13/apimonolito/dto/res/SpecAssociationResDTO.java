package com.squad13.apimonolito.dto.res;

import com.squad13.apimonolito.util.AssociativeId;

public record SpecAssociationResDTO(
        AssociativeId relId,
        Long specId,
        String name,
        boolean isActive,
        boolean isRequired
) { }