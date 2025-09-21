package com.squad13.apimonolito.util.enums;

import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import lombok.*;

@Getter
public enum CompositorEnum {

    AMBIENTE(ItemAmbiente.class),
    MATERIAL(MarcaMaterial.class);

    private final Class<?> clazz;

    CompositorEnum(Class<?> clazz) {
        this.clazz = clazz;
    }
}