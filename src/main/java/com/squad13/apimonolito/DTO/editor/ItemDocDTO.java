package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.enums.LocalEnum;

public record ItemDocDTO(
        Long catalogId,
        String name,
        String desc,
        String itemType,
        DocElementEnum type
) { }