package com.squad13.apimonolito.DTO.catalog.edit;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditCatalogoDTO {

    @NotNull(message = "O ID do catálogo é obrigatório.")
    private Long id;

    private String name;

    private Boolean isActive;
}