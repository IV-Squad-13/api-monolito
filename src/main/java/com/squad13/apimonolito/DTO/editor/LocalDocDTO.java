package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.validation.constraints.NotNull;

public record LocalDocDTO(

        @NotNull(message = "O nome do local é obrigatório")
        LocalEnum local,

        @NotNull(message = "Informe o tipo de elemento a ser adicionado")
        DocElementEnum type
) { }