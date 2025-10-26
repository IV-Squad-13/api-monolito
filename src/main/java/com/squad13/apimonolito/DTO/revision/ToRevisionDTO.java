package com.squad13.apimonolito.DTO.revision;

import com.squad13.apimonolito.util.enums.rule.RevisionRule;
import jakarta.validation.constraints.NotNull;

public record ToRevisionDTO(
        @NotNull(message = "Atribua um Revisor para continuar")
        Long revisorId,

        @NotNull(message = "Informe a regra de inicio da Revisão")
        RevisionRule rule
) { }