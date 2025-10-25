package com.squad13.apimonolito.DTO.revision;

import jakarta.validation.constraints.NotNull;

public record ToRevisionDTO(
        @NotNull(message = "Atribua um Revisor para continuar")
        Long revisorId
) { }