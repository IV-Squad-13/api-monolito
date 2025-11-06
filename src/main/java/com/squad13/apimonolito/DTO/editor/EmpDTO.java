package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.DocInitializationEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmpDTO(

        @NotBlank(message = "O nome do empreendimento é obrigatório")
        @Size(max = 80, message = "O nome do empreendimento não pose ultrapassar 80 caracteres.")
        String name,

        Long padraoId,

        @NotNull(message = "É necessário informar o criador")
        Long creatorId,

        Long empImportId,

        String docImportId,

        @NotNull(message = "Informe a forma de criação da Especificação")
        DocInitializationEnum init
) { }