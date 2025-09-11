package com.squad13.apimonolito.dto.req;

import com.squad13.apimonolito.util.enums.CatalogSpecEnum;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record SpecReqDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 40, message = "Nomes não podem ser maiores de 40 caracteres")
        String name,

        @NotNull(message = "Tipo de elemento de catálogo inválido")
        CatalogSpecEnum specType,

        @Null
        String itemDesc,

        @Null
        LocalEnum localEnum
) { }