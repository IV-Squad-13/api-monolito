package com.squad13.apimonolito.util.enums;

import com.squad13.apimonolito.exceptions.InvalidAttributeException;

import java.util.Arrays;

public enum EmpStatusEnum {
    EM_ELABORACAO, EM_REVISAO, FINALIZADO, CANCELADO;

    public static EmpStatusEnum fromString(String value) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidAttributeException("Empreendimento inválido: " + value));
    }
}