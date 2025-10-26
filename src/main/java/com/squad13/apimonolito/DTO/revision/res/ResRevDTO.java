package com.squad13.apimonolito.DTO.revision.res;

import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
import com.squad13.apimonolito.models.revision.mongo.EspecificacaoRevDocElement;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;

import java.util.List;

public record ResRevDTO(
        Long id,
        RevisaoStatusEnum status,
        List<EspecificacaoRevDocElement> revDocs,
        ResEmpDTO empreendimento
) {

    public static ResRevDTO toDTO(Revisao rev, List<EspecificacaoRevDocElement> revDocs, ResEmpDTO emp) {
        return new ResRevDTO(
                rev.getId(),
                rev.getStatus(),
                revDocs,
                emp
        );
    }
}