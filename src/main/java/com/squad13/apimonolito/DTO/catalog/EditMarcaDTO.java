package com.squad13.apimonolito.DTO.catalog;

import lombok.Data;

@Data
public class EditMarcaDTO {
    private String name;
    private Boolean isActive;
    private Long typeId;
}
