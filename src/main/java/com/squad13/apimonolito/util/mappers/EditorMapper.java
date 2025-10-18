package com.squad13.apimonolito.util.mappers;

import com.squad13.apimonolito.DTO.auth.res.ResUserDTO;
import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.res.*;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
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
                loadDTO.isLoadDocument() ? docs : null,
                loadDTO.isLoadRevision() ? revisions : null,
                users
        );
    }

    public DocElement fromCatalog(EspecificacaoDoc espec, Object entity, DocElementEnum type) {
        return switch (type) {
            case AMBIENTE -> fromCatalog(espec, (Ambiente) entity);
            case ITEM -> fromCatalog(espec, (ItemDesc) entity);
            case MATERIAL -> fromCatalog(espec, (Material) entity);
            case MARCA -> fromCatalog(espec, (Marca) entity);
        };
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

    public AmbienteDocElement fromResponse(EspecificacaoDoc e, ResAmbienteDTO resAmbienteDTO) {
        AmbienteDocElement ambiente = new AmbienteDocElement();
        ambiente.setName(resAmbienteDTO.name());
        ambiente.setLocal(resAmbienteDTO.local());
        ambiente.setEspecificacaoDoc(e);
        ambiente.setCatalogId(resAmbienteDTO.id());
        ambiente.setInSync(true);

        return ambiente;
    }

    public ItemDocElement fromCatalog(EspecificacaoDoc espec, ItemDesc catalogItem) {
        ItemDocElement item = new ItemDocElement();
        item.setName(catalogItem.getName());

        if (catalogItem.getType() != null) {
            item.setTypeId(catalogItem.getType().getId());
            item.setType(catalogItem.getType().getName());
        }

        item.setDesc(catalogItem.getDesc());
        item.setCatalogId(catalogItem.getId());
        item.setEspecificacaoDoc(espec);
        item.setInSync(true);

        return item;
    }

    public ItemDocElement fromResponse(EspecificacaoDoc e, ResItemDTO resItemDTO) {
        ItemDocElement item = new ItemDocElement();
        item.setName(resItemDTO.name());
        item.setDesc(resItemDTO.desc());

        if (resItemDTO.type() != null) {
            item.setTypeId(resItemDTO.type().id());
            item.setType(resItemDTO.type().name());
        }

        item.setEspecificacaoDoc(e);
        item.setCatalogId(resItemDTO.id());
        item.setInSync(true);

        return item;
    }

    public MaterialDocElement fromCatalog(EspecificacaoDoc espec, Material catalogMaterial) {
        MaterialDocElement material = new MaterialDocElement();
        material.setName(catalogMaterial.getName());
        material.setInSync(true);
        material.setEspecificacaoDoc(espec);
        material.setCatalogId(catalogMaterial.getId());

        return material;
    }

    public MaterialDocElement fromResponse(EspecificacaoDoc e, ResMaterialDTO resMaterialDTO) {
        MaterialDocElement material = new MaterialDocElement();
        material.setName(resMaterialDTO.name());
        material.setEspecificacaoDoc(e);
        material.setCatalogId(resMaterialDTO.id());
        material.setInSync(true);

        return material;
    }

    public MarcaDocElement fromCatalog(EspecificacaoDoc espec, Marca catalogMarca) {
        MarcaDocElement marca = new MarcaDocElement();
        marca.setName(catalogMarca.getName());
        marca.setInSync(true);
        marca.setEspecificacaoDoc(espec);
        marca.setCatalogId(catalogMarca.getId());

        return marca;
    }

    public MarcaDocElement fromResponse(EspecificacaoDoc e, ResMarcaDTO resMarcaDTO) {
        MarcaDocElement marca = new MarcaDocElement();
        marca.setName(resMarcaDTO.name());
        marca.setEspecificacaoDoc(e);
        marca.setCatalogId(resMarcaDTO.id());
        marca.setInSync(true);

        return marca;
    }

    public <K, V, P, C> List<P> structure(
            EspecificacaoDoc espec,
            Map<K, List<V>> grouped,
            BiFunction<EspecificacaoDoc, K, P> parentMapper,
            BiFunction<EspecificacaoDoc, V, C> childMapper,
            BiConsumer<P, List<C>> childSetter
    ) {
        return grouped.entrySet().stream()
                .map(entry -> {
                    P parent = parentMapper.apply(espec, entry.getKey());
                    List<C> children = entry.getValue().stream()
                            .map(v -> childMapper.apply(espec, v))
                            .toList();
                    childSetter.accept(parent, children);
                    return parent;
                })
                .toList();
    }
}