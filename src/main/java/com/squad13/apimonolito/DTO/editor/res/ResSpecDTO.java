package com.squad13.apimonolito.DTO.editor.res;

import java.util.List;

public record ResSpecDTO(
        String id,
        Long empId,
        String name,
        String desc,
        String obs,

        List<ResLocalDocDTO> locais,
        List<ResMatDocDTO> materiais
) { }