package com.squad13.apimonolito.util.enums;

import com.squad13.apimonolito.exceptions.InvalidAttributeException;

import java.util.Arrays;

public enum LocalEnum {
    AREA_COMUM, UNIDADES_PRIVATIVAS;

    public static LocalEnum fromString(String value) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidAttributeException("Local inválido: " + value));
    }
}