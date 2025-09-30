package com.squad13.apimonolito.DTO.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MarcaDTO {

    @NotBlank(message = "O nome da marca é obrigatório.")
    @Size(max = 80, message = "O nome da marca não pode ultrapassar 80 caracteres.")
    private String name;

    private Boolean isActive = true;
}
