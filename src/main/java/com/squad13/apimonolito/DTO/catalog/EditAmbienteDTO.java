package com.squad13.apimonolito.DTO.catalog;

import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditAmbienteDTO {

    @NotNull(message = "o ID do item é obrigatório")
    private Long id;

    private String name;

    private LocalEnum local;

    private Boolean isActive;
}
