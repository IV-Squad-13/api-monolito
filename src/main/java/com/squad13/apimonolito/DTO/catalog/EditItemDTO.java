package com.squad13.apimonolito.DTO.catalog;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditItemDTO {

    @NotNull(message = "O ID do item é obrigatório")
    private Long id;

    private String name;

    private String desc;

    private Long typeId;

    private String type;

    private Boolean isActive;
}
