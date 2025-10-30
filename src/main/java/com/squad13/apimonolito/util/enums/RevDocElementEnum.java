package com.squad13.apimonolito.util.enums;

import com.squad13.apimonolito.DTO.editor.res.ResSpecDTO;
import com.squad13.apimonolito.DTO.revision.res.*;
import com.squad13.apimonolito.exceptions.InvalidDocumentTypeException;
import com.squad13.apimonolito.models.revision.mongo.*;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import lombok.Getter;

@Getter
public enum RevDocElementEnum {
    ESPECIFICACAO(EspecificacaoRevDocElement.class, ResSpecRevDTO.class),
    LOCAL(LocalRevDocElement.class, ResLocalRevDTO.class),
    AMBIENTE(AmbienteRevDocElement.class, ResAmbRevDTO.class),
    ITEM(ItemRevDocElement.class, ResItemRevDTO.class),
    MATERIAL(MaterialRevDocElement.class, ResMatRevDTO.class),
    MARCA(MarcaRevDocElement.class, ResMarRevDTO.class);

    private final Class<? extends RevDocElement> revDocument;
    private final Class<? extends ResRevDocDTO> response;

    RevDocElementEnum(
            Class<? extends RevDocElement> clazz,
            Class<? extends ResRevDocDTO> response
    ) {
        this.revDocument = clazz;
        this.response = response;
    }

    public static RevDocElementEnum fromResponse(ResRevDocDTO dto) {
        if (dto == null) return null;
        return fromResponseClass(dto.getClass());
    }

    public static RevDocElementEnum fromResponseClass(Class<? extends ResRevDocDTO> responseClass) {
        for (RevDocElementEnum element : values()) {
            if (element.response.isAssignableFrom(responseClass)) {
                return element;
            }
        }
        return null;
    }
}