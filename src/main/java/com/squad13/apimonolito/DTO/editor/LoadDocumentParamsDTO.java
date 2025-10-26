package com.squad13.apimonolito.DTO.editor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.ModelAttribute;

@Getter
@Setter
@ParameterObject
@AllArgsConstructor
public class LoadDocumentParamsDTO {

    private boolean loadEspecificacao;
    private boolean loadMateriais;
    private boolean loadMarcas;
    private boolean loadLocais;
    private boolean loadAmbientes;
    private boolean loadItems;
    private boolean loadNested;
    private boolean loadPadrao;
    private boolean loadRevision;
    private boolean loadUsers;

    public LoadDocumentParamsDTO() {
        this(false, false, false, false, false, false, false, false, false, false);
    }

    public static LoadDocumentParamsDTO allFalse() {
        return new LoadDocumentParamsDTO(false, false, false, false, false, false, false, false, false, false);
    }

    @ModelAttribute("loadAll")
    public static LoadDocumentParamsDTO allTrue() {
        return new LoadDocumentParamsDTO(true, true, true, true, true, true, true, true, true, true);
    }
}