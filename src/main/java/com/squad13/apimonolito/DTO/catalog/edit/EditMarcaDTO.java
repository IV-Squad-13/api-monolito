package com.squad13.apimonolito.DTO.catalog.edit;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditMarcaDTO {

    @NotNull(message = "O ID da marca é obrigatório.")
    private Long id;

    private String name;

    private Boolean isActive;
}
