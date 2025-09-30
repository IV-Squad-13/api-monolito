package com.squad13.apimonolito.DTO.catalog;

import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.Data;

@Data
public class EditAmbienteDTO {
    private String name;
    private LocalEnum local;
    private Boolean isActive;
    private Long typeId;
}
