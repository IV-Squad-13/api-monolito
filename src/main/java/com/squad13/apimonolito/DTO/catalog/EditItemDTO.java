package com.squad13.apimonolito.DTO.catalog;

import lombok.Data;

@Data
public class EditItemDTO {
    private Long id;
    private String name;
    private String desc;
    private Boolean isActive;
}
