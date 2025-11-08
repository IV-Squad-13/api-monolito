package com.squad13.apimonolito.DTO.editor.res;

import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResMarDocDTO extends ResDocElementDTO {

    public static ResMarDocDTO fromDoc(MarcaDocElement doc) {
        return ResDocElementDTO.fromDoc(doc, ResMarDocDTO::new);
    }
}