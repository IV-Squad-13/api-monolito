package com.squad13.apimonolito.util.enums;

import com.squad13.apimonolito.models.catalog.*;
import lombok.Getter;

@Getter
public enum CatalogSpecEnum {
    AMBIENTE("ambiente"),
    ITEM("item"),
    MATERIAL("material"),
    MARCA("marca");

    private final String value;

    CatalogSpecEnum(String value) {
        this.value = value;
    }

    public static CatalogSpecEnum fromString(String str) {
        for (CatalogSpecEnum e : CatalogSpecEnum.values()) {
            if (e.value.equalsIgnoreCase(str)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown CatalogSpecEnum: " + str);
    }
}