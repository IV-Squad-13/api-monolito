package com.squad13.apimonolito.util.enums;

import com.squad13.apimonolito.exceptions.InvalidAttributeException;

import java.util.Arrays;

public enum EmpStatusEnum {
    ELABORACAO, SUSPENSO, FINALIZADO, CANCELADO;

    public static EmpStatusEnum fromString(String value) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidAttributeException("Empreendimento inválido: " + value));
    }
}