package com.squad13.apimonolito.DTO.auth.res;

import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.models.user.associative.UsuarioEmpreendimento;

public record ResUserDTO(
        Long id,
        String name,
        String email,
        String role
) {

    public static ResUserDTO from(UsuarioEmpreendimento userEmp) {
        Usuario user =  userEmp.getUsuario();
        return new ResUserDTO(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getPapel().getNome()
        );
    }

    public static ResUserDTO from(Usuario user) {
        return new ResUserDTO(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getPapel().getNome()
        );
    }
}