package com.squad13.apimonolito.DTO.revision.res;

import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;
import com.squad13.apimonolito.util.mapper.EditorMapper;

public record ResRevDTO(
        Long id,
        RevisaoStatusEnum status,
        ResEmpDTO empreendimento
) {

    public static ResRevDTO toDTO(Revisao rev, ResEmpDTO emp) {
        return new ResRevDTO(
                rev.getId(),
                rev.getStatusEnum(),
                emp
        );
    }
}