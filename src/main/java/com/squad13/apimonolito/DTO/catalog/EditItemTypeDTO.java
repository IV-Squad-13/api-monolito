package com.squad13.apimonolito.DTO.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditItemTypeDTO {
    @NotNull(message = "O ID do item é obrigatório.")
    private Long id;

    private String name;

    private Boolean isActive;
}