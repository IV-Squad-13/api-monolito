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

import java.util.function.Supplier;

@Getter
public enum DocElementEnum {
    AMBIENTE(Ambiente.class, AmbienteDocElement.class, ResAmbDocDTO.class, ResAmbDocDTO::new),
    ITEM(ItemDesc.class, ItemDocElement.class, ResItemDocDTO.class, ResItemDocDTO::new),
    MATERIAL(Material.class, MaterialDocElement.class, ResMatDocDTO.class, ResMatDocDTO::new),
    MARCA(Marca.class, MarcaDocElement.class, ResMarDocDTO.class, ResMarDocDTO::new);

    private final Class<?> catalogEntity;
    private final Class<? extends DocElement> docElement;
    private final Class<? extends ResDocElementDTO> resDocDTO;
    private final Supplier<? extends ResDocElementDTO> resDocSupplier;

    DocElementEnum(
            Class<?> catalogEntity,
            Class<? extends DocElement> docElement,
            Class<? extends ResDocElementDTO> resDocDTO,
            Supplier<? extends ResDocElementDTO> resDocSupplier
    ) {
        this.catalogEntity = catalogEntity;
        this.docElement = docElement;
        this.resDocDTO = resDocDTO;
        this.resDocSupplier = resDocSupplier;
    }
}