package com.squad13.apimonolito.DTO.editor.edit;

import com.squad13.apimonolito.util.enums.DocInitializationEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditEspecificacaoDocDTO(
        String name,
        String desc,
        String obs,
        Long empId
) { }
