package com.squad13.apimonolito.DTO.editor.res;

import com.squad13.apimonolito.DTO.auth.res.ResUserDTO;
import com.squad13.apimonolito.DTO.auth.res.ResUserEmpDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResPadraoDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDTO;
import com.squad13.apimonolito.util.enums.DocInitializationEnum;
import com.squad13.apimonolito.util.enums.EmpStatusEnum;

import java.time.Instant;
import java.util.List;

public record ResEmpDTO(
        Long id,
        String name,
        EmpStatusEnum status,
        DocInitializationEnum init,
        Instant createdAt,

        ResPadraoDTO padrao,
        ResSpecDTO refDoc,
        ResSpecDTO doc,
        ResRevDTO revision,
        List<ResUserEmpDTO> users,
        ResUserEmpDTO creator
) { }