package com.squad13.apimonolito.DTO.editor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@Setter
@ParameterObject
@AllArgsConstructor
public class ElementCreationParamsDTO {

    private boolean byCatalog;
    private boolean association;
    private boolean includeAssociations;

    public ElementCreationParamsDTO() {
        this(false, false, false);
    }

    public static ElementCreationParamsDTO allFalse() {
        return new ElementCreationParamsDTO(false, false, false);
    }

    public static ElementCreationParamsDTO allTrue() {
        return new ElementCreationParamsDTO(true, true, true);
    }
}