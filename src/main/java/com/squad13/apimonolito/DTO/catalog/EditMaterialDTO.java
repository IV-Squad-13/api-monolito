package com.squad13.apimonolito.DTO.catalog;

import lombok.Data;

@Data
public class EditMaterialDTO {
    private Long id;
    private String name;
    private Boolean isActive;
}

