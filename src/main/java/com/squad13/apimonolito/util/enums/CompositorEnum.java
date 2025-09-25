package com.squad13.apimonolito.util.enums;

import com.squad13.apimonolito.exceptions.InvalidCompositorException;

import java.util.Arrays;

public enum CompositorEnum {
    AMBIENTE, MATERIAL;

    public static CompositorEnum fromString(String value) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidCompositorException("Compositor inválido: " + value));
    }
}