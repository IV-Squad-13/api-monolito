package com.squad13.apimonolito.DTO.editor.res;

import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResAmbDocDTO extends ResDocElementDTO {
    private LocalEnum local;
    private List<ResItemDocDTO> items = new ArrayList<>();
}