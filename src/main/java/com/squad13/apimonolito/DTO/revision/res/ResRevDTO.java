package com.squad13.apimonolito.DTO.revision.res;

import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;

import java.util.Collections;
import java.util.List;

public record ResRevDTO(
        Long id,
        RevisaoStatusEnum status,
        List<? extends ResRevDocDTO> revDocs,
        ResEmpDTO empreendimento
) {
    public static ResRevDTO from(Revisao rev, List<? extends ResRevDocDTO> revDocs, ResEmpDTO emp) {
        return new ResRevDTO(
                rev.getId(),
                rev.getStatus(),
                revDocs != null ? revDocs : Collections.emptyList(),
                emp
        );
    }
}