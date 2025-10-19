package com.squad13.apimonolito.DTO.editor.res;

import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResAmbDocDTO extends ResDocElementDTO {
    private LocalEnum local;
    private List<ResItemDocDTO> items;

    public ResAmbDocDTO(ResDocElementDTO base, LocalEnum local, List<ResItemDocDTO> items) {
        super(base);
        this.local = local;
        this.items = items;
    }
}