package com.squad13.apimonolito.DTO.editor.edit;

import java.util.List;

public record EditEspecificacaoDocDTO(
        String name,
        String desc,
        String local,
        List<String> obs,
        Long empId
) {
}
