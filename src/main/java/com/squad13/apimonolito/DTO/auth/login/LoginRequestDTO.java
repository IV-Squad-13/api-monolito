package com.squad13.apimonolito.DTO.auth.login;

public record LoginRequestDTO(
        String username,
        String email,
        String password
) {}
