package com.squad13.apimonolito.dto.req;

import jakarta.validation.constraints.NotBlank;

public record TempUserReqDTO(
        @NotBlank(message = "Email é obrigatório")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String password
) { }