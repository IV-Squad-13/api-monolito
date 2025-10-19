package com.squad13.apimonolito.DTO.editor.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResMarDocDTO extends ResDocElementDTO {

    public ResMarDocDTO(ResMarDocDTO base) {
        super(base);
    }
}