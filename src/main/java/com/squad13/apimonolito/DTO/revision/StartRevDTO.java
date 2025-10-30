package com.squad13.apimonolito.DTO.revision;

import jakarta.validation.constraints.NotNull;

public record StartRevDTO(
        @NotNull(message = "Informe o Usuário que iniciará a Revisão")
        Long revisorId
) { }