package com.squad13.apimonolito.util.mappers;

import com.squad13.apimonolito.DTO.auth.res.ResUserDTO;
import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResPadraoDTO;
import com.squad13.apimonolito.DTO.editor.AmbienteDocDTO;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.DTO.editor.ItemDocDTO;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.models.catalog.*;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.models.revision.mongo.EspecificacaoRevDocElement;
import com.squad13.apimonolito.util.DocumentSearch;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EditorMapper {

    private final DocumentSearch docSearch;

    private final CatalogMapper catalogMapper;
    private final UserMapper userMapper;

    public ResEmpDTO toResponse(
            Empreendimento emp,
            List<EspecificacaoDoc> docs,
            List<EspecificacaoRevDocElement> revisions,
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

    public AmbienteDocElement fromCatalog(EspecificacaoDoc espec, Ambiente catalogAmbiente) {
        AmbienteDocElement ambiente = new AmbienteDocElement();
        ambiente.setName(catalogAmbiente.getName());
        ambiente.setLocal(catalogAmbiente.getLocal());
        ambiente.setInSync(true);
        ambiente.setEspecificacaoDoc(espec);
        ambiente.setCatalogId(catalogAmbiente.getId());

        return ambiente;
    }

    public ItemDocElement fromCatalog(EspecificacaoDoc espec, ItemDesc catalogItem) {
        ItemDocElement item = new ItemDocElement();
        item.setName(catalogItem.getName());
        item.setType(catalogItem.getType().getName());
        item.setDesc(catalogItem.getDesc());
        item.setCatalogId(catalogItem.getId());
        item.setEspecificacaoDoc(espec);
        item.setInSync(true);

        return item;
    }

    public MarcaDocElement fromCatalog(EspecificacaoDoc espec, Marca catalogMarca) {
        MarcaDocElement marca = new MarcaDocElement();
        marca.setName(catalogMarca.getName());
        marca.setInSync(true);
        marca.setEspecificacaoDoc(espec);
        marca.setCatalogId(catalogMarca.getId());

        return marca;
    }

    public MaterialDocElement fromCatalog(EspecificacaoDoc espec, Material catalogMaterial) {
        MaterialDocElement material = new MaterialDocElement();
        material.setName(catalogMaterial.getName());
        material.setInSync(true);
        material.setEspecificacaoDoc(espec);
        material.setCatalogId(catalogMaterial.getId());

        return material;
    }
}