package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmbienteDocDTO extends DocElementDTO {

    @NotNull(message = "Informe o local do ambiente")
    private LocalEnum local;

    @Override
    public DocElementEnum getDocType() {
        return DocElementEnum.AMBIENTE;
    }
}