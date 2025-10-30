package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.DocElementEnum;
import jakarta.validation.constraints.NotNull;

public record DocElementCatalogCreationDTO(

        @NotNull(message = "Informe um tipo válido para o documento")
        DocElementEnum type,

        @NotNull(message = "Informe o ID do elemento no catálogo")
        Long elementId,

        String associatedId
) {
}