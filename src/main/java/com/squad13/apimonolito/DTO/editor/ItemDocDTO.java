package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.DocElementEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDocDTO extends DocElementDTO {

    @NotBlank(message = "Informe a descrição do item")
    private String desc;

    @NotNull(message = "Informe o tipo do item")
    private Long typeId;

    @Override
    public DocElementEnum getDocType() {
        return DocElementEnum.ITEM;
    }
}