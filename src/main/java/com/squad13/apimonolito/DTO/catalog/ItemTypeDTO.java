package com.squad13.apimonolito.DTO.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemTypeDTO {
    @NotBlank(message = "O nome do tipo de item é obrigatório.")
    @Size(max = 80, message = "O nome do tipo de item não pode ultrapassar 80 caracteres.")
    private String name;

    private boolean isActive = true;
}