package com.squad13.apimonolito.util.builder;

import com.squad13.apimonolito.DTO.editor.AmbienteDocDTO;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.DTO.editor.ItemDocDTO;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.exceptions.InvalidDocumentTypeException;
import com.squad13.apimonolito.models.catalog.ItemType;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.services.editor.SynchronizationService;
import com.squad13.apimonolito.util.factory.ResponseDocFactory;
import com.squad13.apimonolito.util.search.CatalogSearch;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DocElementBuilder {

    private final CatalogSearch catalogSearch;
    private final SynchronizationService syncService;

    private final ResponseDocFactory resDocFactory;

    public Aggregation buildAggregation(LoadDocumentParamsDTO params) {
        if (params == null) {
            return Aggregation.newAggregation(Aggregation.match(new Criteria()));
        }

        List<AggregationOperation> operations = new ArrayList<>();

        if (params.isLoadLocais()) {
            operations.add(resDocFactory.lookupLocais(params));
        }

        if (params.isLoadItems()) {
            operations.add(resDocFactory.lookupItems(params));
        }

        if (params.isLoadMateriais()) {
            operations.add(resDocFactory.lookupMateriais(params));
        }

        if (params.isLoadMarcas()) {
            operations.add(resDocFactory.lookupMarcas(params));
        }

        if (operations.isEmpty()) {
            operations.add(Aggregation.match(new Criteria()));
        }

        return Aggregation.newAggregation(operations);
    }

    @ExceptionHandler(InvalidDocumentTypeException.class)
    public DocElement create(ObjectId specId, DocElementDTO dto) {
        return switch (dto.getDocType()) {
            case AMBIENTE -> buildAmbiente(specId, (AmbienteDocDTO) dto);
            case ITEM -> buildItem(specId, (ItemDocDTO) dto);
            case MATERIAL -> buildMaterial(specId, dto);
            case MARCA -> buildMarca(specId, dto);
        };
    }

    private AmbienteDocElement buildAmbiente(ObjectId specId, AmbienteDocDTO dto) {
        AmbienteDocElement ambiente = AmbienteDocElement.fromDto(dto, specId);
        setSyncStatus(ambiente, dto.getDocType());
        return ambiente;
    }

    private ItemDocElement buildItem(ObjectId specId, ItemDocDTO dto) {
        ItemType type = catalogSearch.findInCatalog(dto.getTypeId(), ItemType.class);
        ItemDocElement item = ItemDocElement.fromDto(dto, specId, type);
        setSyncStatus(item, dto.getDocType());
        return item;
    }

    private MaterialDocElement buildMaterial(ObjectId specId, DocElementDTO dto) {
        MaterialDocElement material = MaterialDocElement.fromDto(dto, specId);
        setSyncStatus(material, dto.getDocType());
        return material;
    }

    private MarcaDocElement buildMarca(ObjectId specId, DocElementDTO dto) {
        MarcaDocElement marca = MarcaDocElement.fromDto(specId, dto);
        setSyncStatus(marca, dto.getDocType());
        return marca;
    }

    private void setSyncStatus(DocElement element, DocElementEnum type) {
        if (element.getCatalogId() != null) {
            element.setInSync(syncService.inSync(element, type));
        }
    }
}