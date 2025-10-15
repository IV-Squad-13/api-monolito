package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.DocInitializationEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EspecificacaoDocDTO(
        @NotBlank(message = "A Especificação precisa ter um nome")
        String name,

        String desc,
        String obs,

        @NotNull(message = "A Especificação precisa ter o id do Empreendimento")
        Long empId,

        @NotNull(message = "Informe o tipo de inicialização para a Especificação")
        DocInitializationEnum initType
) { }