package com.squad13.apimonolito.DTO.catalog;

import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.ModelAttribute;

@Getter
@Setter
@ParameterObject
public class LoadParametersDTO {

    private boolean loadPadroes;
    private boolean loadAmbientes;
    private boolean loadItems;
    private boolean loadMateriais;
    private boolean loadMarcas;

    public LoadParametersDTO() {
        this(false, false, false, false, false);
    }

    public LoadParametersDTO(
            boolean loadPadroes,
            boolean loadAmbientes,
            boolean loadItems,
            boolean loadMateriais,
            boolean loadMarcas
    ) {
        this.loadPadroes = loadPadroes;
        this.loadAmbientes = loadAmbientes;
        this.loadItems = loadItems;
        this.loadMateriais = loadMateriais;
        this.loadMarcas = loadMarcas;
    }

    public static LoadParametersDTO allFalse() {
        return new LoadParametersDTO(false, false, false, false, false);
    }

    @ModelAttribute("loadAll")
    public static LoadParametersDTO allTrue() {
        return new LoadParametersDTO(true, true, true, true, true);
    }
}