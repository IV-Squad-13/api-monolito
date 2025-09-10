package com.squad13.apimonolito.dto.res;

public record TempUserResDTO(
        Long id,
        String name,
        String email,
        String password,
        String role,
        String createdAt
) { }