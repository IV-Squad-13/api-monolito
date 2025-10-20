package com.squad13.apimonolito.DTO.editor.res;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResMatDocDTO extends ResDocElementDTO {
    private List<ResMarDocDTO> marcas = new ArrayList<>();
}