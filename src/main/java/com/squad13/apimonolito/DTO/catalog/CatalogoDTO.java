package com.squad13.apimonolito.DTO.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CatalogoDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 80, message = "O nome não deve ultrapassar 80 caracteres.")
    private String name;

    private boolean isActive = true;
}