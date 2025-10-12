package com.squad13.apimonolito.util.enums;

import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.Getter;

import java.util.Arrays;
import java.util.function.Supplier;

@Getter
public enum DocElementEnum {

    AMBIENTE(AmbienteDocElement::new, Ambiente.class),
    MARCA(MarcaDocElement::new, Marca.class),
    MATERIAL(MaterialDocElement::new, Material.class),
    ITEM(ItemDocElement::new, ItemDesc.class),
    LOCAL(LocalDocElement::new, null);

    private final Supplier<? extends DocElement> factory;
    private final Class<?> catalogClass;

    DocElementEnum(Supplier<? extends DocElement> factory, Class<?> catalogClass) {
        this.factory = factory;
        this.catalogClass = catalogClass;
    }

    public DocElement newInstance() {
        return factory.get();
    }

    public boolean hasCatalog() {
        return catalogClass != null;
    }
}