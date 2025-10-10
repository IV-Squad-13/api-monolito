package com.squad13.apimonolito.DTO.catalog.edit;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditItemDTO {
    private String name;

    private String desc;

    private Long typeId;

    private String type;

    private Boolean isActive;
}
