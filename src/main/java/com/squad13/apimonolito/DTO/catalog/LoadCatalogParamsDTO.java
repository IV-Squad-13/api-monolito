package com.squad13.apimonolito.DTO.catalog;

import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.ModelAttribute;

@Getter
@Setter
@ParameterObject
public class LoadCatalogParamsDTO {

    private boolean loadAll;
    private boolean loadPadroes;
    private boolean loadAmbientes;
    private boolean loadItems;
    private boolean loadMateriais;
    private boolean loadMarcas;
    private boolean loadNested; // PERIGO!!!!!

    public LoadCatalogParamsDTO(
            boolean loadAll,
            boolean loadPadroes,
            boolean loadAmbientes,
            boolean loadItems,
            boolean loadMateriais,
            boolean loadMarcas,
            boolean loadNested
    ) {
        this.loadAll = loadAll;
        this.loadPadroes = loadPadroes;
        this.loadAmbientes = loadAmbientes;
        this.loadItems = loadItems;
        this.loadMateriais = loadMateriais;
        this.loadMarcas = loadMarcas;
        this.loadNested = loadNested;
    }

    public LoadCatalogParamsDTO() {
        this(false, false, false, false, false, false, false);
    }

    public static LoadCatalogParamsDTO allFalse() {
        return new LoadCatalogParamsDTO(false, false, false, false, false, false, false);
    }

    @ModelAttribute("loadAll")
    public static LoadCatalogParamsDTO allTrue() {
        return new LoadCatalogParamsDTO(true, true, true, true, true, true, true);
    }
}