package com.squad13.apimonolito.DTO.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaterialDTO {

    @NotBlank(message = "O nome da material é obrigatório.")
    @Size(max = 80, message = "O nome da material não pode ultrapassar 80 caracteres.")
    private String name;

    private Boolean isActive;
}

