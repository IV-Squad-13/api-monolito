package com.squad13.apimonolito.util.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Getter
public enum AccessEnum {

    CRIADOR(PapelEnum.ADMIN, PapelEnum.RELATOR),
    RELATOR(PapelEnum.ADMIN, PapelEnum.RELATOR),
    LEITOR(PapelEnum.ADMIN, PapelEnum.REVISOR, PapelEnum.RELATOR),
    REVISOR(PapelEnum.ADMIN, PapelEnum.REVISOR);

    private final Set<PapelEnum> allowedRoles;

    AccessEnum(PapelEnum... roles) {
        this.allowedRoles = Collections.unmodifiableSet(EnumSet.of(roles[0], Arrays.copyOfRange(roles, 1, roles.length)));
    }

    public boolean notAllowed(PapelEnum role) {
        return !allowedRoles.contains(role);
    }
}