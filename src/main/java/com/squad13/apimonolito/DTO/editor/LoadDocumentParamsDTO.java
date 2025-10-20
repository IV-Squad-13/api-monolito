package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.ModelAttribute;

@Getter
@Setter
@ParameterObject
public class LoadDocumentParamsDTO {

    private boolean loadEspecificacao;
    private boolean loadMateriais;
    private boolean loadMarcas;
    private boolean loadLocais;
    private boolean loadAmbientes;
    private boolean loadItems;
    private boolean loadPadrao;
    private boolean loadRevision;
    private boolean loadUsers;

    public LoadDocumentParamsDTO(
            boolean loadEspecificacao,
            boolean loadMateriais,
            boolean loadMarcas,
            boolean loadLocais,
            boolean loadAmbientes,
            boolean loadItems,
            boolean loadPadrao,
            boolean loadRevision,
            boolean loadUsers
    ) {
        this.loadEspecificacao = loadEspecificacao;
        this.loadMateriais = loadMateriais;
        this.loadMarcas = loadMarcas;
        this.loadLocais = loadLocais;
        this.loadAmbientes = loadAmbientes;
        this.loadItems = loadItems;
        this.loadPadrao = loadPadrao;
        this.loadRevision = loadRevision;
        this.loadUsers = loadUsers;
    }

    public static LoadDocumentParamsDTO allFalse() {
        return new LoadDocumentParamsDTO(false, false, false, false, false, false, false, false, false);
    }

    public static LoadDocumentParamsDTO allTrue() {
        return new LoadDocumentParamsDTO(
                true, true, true, true, true, true, true, true, true
        );
    }
}