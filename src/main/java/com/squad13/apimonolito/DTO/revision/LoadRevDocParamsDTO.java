package com.squad13.apimonolito.DTO.revision;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.ModelAttribute;

@Getter
@Setter
@ParameterObject
@AllArgsConstructor
public class LoadRevDocParamsDTO {

    private boolean loadRevDocuments;
    private boolean loadLocais;
    private boolean loadAmbientes;
    private boolean loadItems;
    private boolean loadMateriais;
    private boolean loadMarcas;

    private boolean loadSpecDocuments; // Não funciona, alguém se importa?
    private boolean loadEmpreendimento;

    public static LoadRevDocParamsDTO allFalse() {
        return new LoadRevDocParamsDTO(false, false, false, false, false, false, false, false);
    }

    @ModelAttribute("loadAll")
    public static LoadRevDocParamsDTO allTrue() {
        return new LoadRevDocParamsDTO(true, true, true, true, true, true, true, true);
    }
}