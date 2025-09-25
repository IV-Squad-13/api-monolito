package com.squad13.apimonolito.DTO.catalog;

import lombok.Data;

@Data
public class ItemDTO {
    private String name;
    private Boolean isActive;
    private Long typeId;
    private String width;
    private String height;
    private String desc;
}
