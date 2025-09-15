package com.squad13.apimonolito.DTO;

import lombok.Data;

@Data
public class ItemDTO {
    private Long id;
    private String name;
    private Boolean isActive;
    private Long typeId;
    private String width;
    private String height;
    private String desc;
}
