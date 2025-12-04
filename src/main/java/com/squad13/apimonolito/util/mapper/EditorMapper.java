package com.squad13.apimonolito.util.mapper;

import com.squad13.apimonolito.DTO.auth.res.ResUserDTO;
import com.squad13.apimonolito.DTO.auth.res.ResUserEmpDTO;
import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResPadraoDTO;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
import com.squad13.apimonolito.DTO.editor.res.ResSpecDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDTO;
import com.squad13.apimonolito.exceptions.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.util.enums.AccessEnum;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.enums.DocInitializationEnum;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EditorMapper {

    private final CatalogMapper catalogMapper;
    private final UserMapper userMapper;

    public ResEmpDTO toResponse(
            Empreendimento emp,
            ResSpecDTO doc,
            ResSpecDTO refDoc,
            ResRevDTO revDTO,
            LoadDocumentParamsDTO loadParams
    ) {
        if (emp == null) return null;

        ResPadraoDTO padrao = emp.getInit().equals(DocInitializationEnum.PADRAO) && (loadParams.isLoadPadrao() || loadParams.isLoadAll())
                ? catalogMapper.toResponse(emp.getPadrao(), LoadCatalogParamsDTO.allFalse())
                : null;

        List<ResUserEmpDTO> users = loadParams.isLoadUsers() || loadParams.isLoadAll()
                ? userMapper.getUsersEmpDTO(emp.getUsuarioSet())
                : null;

        ResUserEmpDTO creator = ResUserEmpDTO.from(
                emp.getUsuarioSet().stream()
                        .filter(u -> AccessEnum.CRIADOR.equals(u.getAccessLevel()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Empreendimento não possuí um criador"))
        );

        return new ResEmpDTO(
                emp.getId(),
                emp.getName(),
                emp.getStatus(),
                emp.getInit(),
                emp.getCreatedAt(),
                padrao,
                refDoc,
                loadParams.isLoadEspecificacao() || loadParams.isLoadAll() ? doc : null,
                loadParams.isLoadRevision() || loadParams.isLoadAll() ? revDTO : null,
                users,
                creator
        );
    }

    public ResSpecDTO toResponse(EspecificacaoDoc doc) {
        return new ResSpecDTO(
                doc.getId(),
                doc.getEmpreendimentoId(),
                doc.getName(),
                doc.getDesc(),
                doc.getLocal(),
                doc.getObs(),
                List.of(),
                List.of()
        );
    }

    public DocElement fromCatalog(ObjectId specId, Object entity, ObjectId parentId, DocElementEnum type) {
        return switch (type) {
            case AMBIENTE -> fromCatalog(specId, (Ambiente) entity);
            case ITEM -> fromCatalog(specId, (ItemDesc) entity, parentId);
            case MATERIAL -> fromCatalog(specId, (Material) entity, parentId);
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

    public ItemDocElement fromCatalog(ObjectId specId, ItemDesc catalogItem, ObjectId parentId) {
        ItemDocElement item = new ItemDocElement();
        item.setName(catalogItem.getName());

        if (catalogItem.getType() != null) {
            item.setTypeId(catalogItem.getType().getId());
            item.setType(catalogItem.getType().getName());
        }

        if (parentId != null) {
            item.setParentId(parentId);
        }

        item.setDesc(catalogItem.getDesc());
        item.setCatalogId(catalogItem.getId());
        item.setEspecificacaoId(specId);
        item.setInSync(true);

        return item;
    }

    public MaterialDocElement fromCatalog(ObjectId specId, Material catalogMaterial, ObjectId parentId) {
        MaterialDocElement material = new MaterialDocElement();
        material.setName(catalogMaterial.getName());
        material.setInSync(true);
        material.setEspecificacaoId(specId);
        material.setCatalogId(catalogMaterial.getId());

        if (parentId != null) {
            material.setParentId(parentId);
        }

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