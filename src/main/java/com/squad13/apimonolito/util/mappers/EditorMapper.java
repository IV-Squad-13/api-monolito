package com.squad13.apimonolito.util.mappers;

import com.squad13.apimonolito.DTO.auth.res.ResUserDTO;
import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.res.*;
import com.squad13.apimonolito.DTO.editor.ItemDocDTO;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.res.*;
import com.squad13.apimonolito.models.catalog.*;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.models.revision.mongo.EspecificacaoRevDocElement;
import com.squad13.apimonolito.util.DocumentSearch;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

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
                loadDTO.isLoadEspecificacao() ? docs : null,
                loadDTO.isLoadRevision() ? revisions : null,
                users
        );
    }

    public DocElement fromCatalog(ObjectId specId, Object entity, DocElementEnum type) {
        return switch (type) {
            case AMBIENTE -> fromCatalog(specId, (Ambiente) entity);
            case ITEM -> fromCatalog(specId, (ItemDesc) entity);
            case MATERIAL -> fromCatalog(specId, (Material) entity);
            case MARCA -> fromCatalog(specId, (Marca) entity);
        };
    }

    public AmbienteDocElement fromCatalog(ObjectId specId, Ambiente catalogAmbiente) {
        AmbienteDocElement ambiente = new AmbienteDocElement();
        ambiente.setName(catalogAmbiente.getName());
        ambiente.setLocal(catalogAmbiente.getLocal());
        ambiente.setInSync(true);
        ambiente.setEspecificacaoId(specId);
        ambiente.setCatalogId(catalogAmbiente.getId());

        return ambiente;
    }

    public ItemDocElement fromCatalog(ObjectId specId, ItemDesc catalogItem) {
        ItemDocElement item = new ItemDocElement();
        item.setName(catalogItem.getName());

        if (catalogItem.getType() != null) {
            item.setTypeId(catalogItem.getType().getId());
            item.setType(catalogItem.getType().getName());
        }

        item.setDesc(catalogItem.getDesc());
        item.setCatalogId(catalogItem.getId());
        item.setEspecificacaoId(specId);
        item.setInSync(true);

        return item;
    }

    public MaterialDocElement fromCatalog(ObjectId specId, Material catalogMaterial) {
        MaterialDocElement material = new MaterialDocElement();
        material.setName(catalogMaterial.getName());
        material.setInSync(true);
        material.setEspecificacaoId(specId);
        material.setCatalogId(catalogMaterial.getId());

        return material;
    }

    public MarcaDocElement fromCatalog(ObjectId specId, Marca catalogMarca) {
        MarcaDocElement marca = new MarcaDocElement();
        marca.setName(catalogMarca.getName());
        marca.setInSync(true);
        marca.setEspecificacaoId(specId);
        marca.setCatalogId(catalogMarca.getId());

        return marca;
    }
}