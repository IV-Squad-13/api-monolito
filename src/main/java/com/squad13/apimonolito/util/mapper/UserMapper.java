package com.squad13.apimonolito.util.mapper;

import com.squad13.apimonolito.DTO.auth.res.ResUserDTO;
import com.squad13.apimonolito.models.user.associative.UsuarioEmpreendimento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public List<ResUserDTO> getUsersDTO(Set<UsuarioEmpreendimento> userEmpList) {
        return userEmpList.stream()
                .map(ResUserDTO::from)
                .toList();
    }
}