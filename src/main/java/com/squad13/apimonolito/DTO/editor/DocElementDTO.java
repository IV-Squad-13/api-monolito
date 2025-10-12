package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.enums.LocalEnum;

public record DocElementDTO(
        Long catalogId,
        String especId,
        String name,
        LocalEnum local,
        String desc,

        DocElementEnum type
) { }