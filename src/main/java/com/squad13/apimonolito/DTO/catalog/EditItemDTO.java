package com.squad13.apimonolito.DTO.catalog;

import lombok.Data;

@Data
public class EditItemDTO {
    private String name;
    private String desc;
    private Boolean isActive;
    private Long typeId;
}
