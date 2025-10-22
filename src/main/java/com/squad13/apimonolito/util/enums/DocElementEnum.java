package com.squad13.apimonolito.util.enums;

import com.squad13.apimonolito.DTO.editor.res.*;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.Getter;

@Getter
public enum DocElementEnum {
    AMBIENTE(Ambiente.class, AmbienteDocElement.class, ResAmbDocDTO.class),
    MARCA(Marca.class, MarcaDocElement.class, ResMarDocDTO.class),
    MATERIAL(Material.class, MaterialDocElement.class, ResMatDocDTO.class),
    ITEM(ItemDesc.class, ItemDocElement.class, ResItemDocDTO.class);

    private final Class<?> catalogEntity;
    private final Class<? extends DocElement> docElement;
    private final Class<? extends ResDocElementDTO> resDoc;

    DocElementEnum(Class<?> catalogEntity, Class<? extends DocElement> docElement, Class<? extends ResDocElementDTO> resDoc) {
        this.catalogEntity = catalogEntity;
        this.docElement = docElement;
        this.resDoc = resDoc;
    }
}