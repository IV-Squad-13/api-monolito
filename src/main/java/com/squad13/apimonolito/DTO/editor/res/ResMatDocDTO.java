package com.squad13.apimonolito.DTO.editor.res;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResMatDocDTO extends ResDocElementDTO {
    private List<ResMarDocDTO> marcas;

    public ResMatDocDTO(ResDocElementDTO base, List<ResMarDocDTO> marcas) {
        super(base);
        this.marcas = marcas;
    }
}