package com.squad13.apimonolito.DTO.auth.res;

import com.squad13.apimonolito.models.user.associative.UsuarioEmpreendimento;
import com.squad13.apimonolito.util.enums.AccessEnum;

public record ResUserEmpDTO(
        ResUserDTO user,
        AccessEnum access
) {

    public static ResUserEmpDTO from(UsuarioEmpreendimento userEmp) {
        ResUserDTO user = ResUserDTO.from(userEmp);
        return new ResUserEmpDTO(
                user,
                userEmp.getAccessLevel()
        );
    }
}
