package com.squad13.apimonolito.DTO.editor.edit;

import com.squad13.apimonolito.util.enums.EmpStatusEnum;

import java.util.List;

public record EditEmpDTO(
        String name,
        EmpStatusEnum status,

        Long padraoId
) { }