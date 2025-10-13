package com.squad13.apimonolito.util.enums;

import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.Getter;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;

import java.util.function.Supplier;

@Getter
public enum DocElementEnum {
    AMBIENTE(Ambiente::new),
    MARCA(Marca::new),
    MATERIAL(Material::new),
    ITEM(ItemDesc::new),
    LOCAL(null);

    private final Supplier<?> factory;

    DocElementEnum(Supplier<?> factory) {
        this.factory = factory;
    }

    public Object newInstance() {
        if (factory == null) return null;
        return factory.get();
    }
}