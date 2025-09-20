package com.squad13.apimonolito.DTO.catalog;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditMaterialDTO {

    @NotNull(message = "O ID do material é obrigatório")
    private Long id;

    private String name;

    private Boolean isActive;
}

