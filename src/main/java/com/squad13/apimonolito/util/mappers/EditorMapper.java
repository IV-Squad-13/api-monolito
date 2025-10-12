package com.squad13.apimonolito.util.mappers;

import com.squad13.apimonolito.DTO.auth.res.ResUserDTO;
import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResPadraoDTO;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.revision.mongo.EspecificacaoRevDoc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EditorMapper {

    private final CatalogMapper catalogMapper;
    private final UserMapper userMapper;

    public ResEmpDTO toResponse(
            Empreendimento emp,
            List<EspecificacaoDoc> docs,
            List<EspecificacaoRevDoc> revisions,
            LoadDocumentParamsDTO loadDTO
    ) {
        if (emp == null) return null;

        ResPadraoDTO padrao = loadDTO.isLoadPadrao()
                ? catalogMapper.toResponse(emp.getPadrao(), LoadCatalogParamsDTO.allTrue())
                : null;

        List<ResUserDTO> users = loadDTO.isLoadUsers()
                ? userMapper.getUsersDTO(emp.getUsuarioList())
                : null;

        return new ResEmpDTO(
                emp.getId(),
                emp.getName(),
                emp.getStatus(),
                padrao,
                loadDTO.isLoadDocument() ? docs : null,
                loadDTO.isLoadRevision() ? revisions : null,
                users
        );
    }

}