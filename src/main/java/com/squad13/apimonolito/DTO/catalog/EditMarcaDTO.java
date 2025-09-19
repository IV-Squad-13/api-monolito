package com.squad13.apimonolito.DTO.catalog;

import lombok.Data;

@Data
public class EditMarcaDTO {
    private Long id;
    private String name;
    private Boolean isActive;
}
