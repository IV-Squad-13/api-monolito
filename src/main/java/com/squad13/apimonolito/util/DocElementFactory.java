package com.squad13.apimonolito.util;

import com.squad13.apimonolito.DTO.editor.AmbienteDocDTO;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.DTO.editor.ItemDocDTO;
import com.squad13.apimonolito.exceptions.InvalidDocumentTypeException;
import com.squad13.apimonolito.models.catalog.ItemType;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.services.editor.SynchronizationService;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@RequiredArgsConstructor
public class DocElementFactory {

    private final CatalogSearch catalogSearch;
    private final SynchronizationService syncService;

    @ExceptionHandler(InvalidDocumentTypeException.class)
    public DocElement create(String specId, DocElementDTO dto) {
        return switch (dto.getDocType()) {
            case AMBIENTE -> buildAmbiente(specId, (AmbienteDocDTO) dto);
            case ITEM -> buildItem(specId, (ItemDocDTO) dto);
            case MATERIAL -> buildMaterial(specId, dto);
            case MARCA -> buildMarca(specId, dto);
        };
    }

    private AmbienteDocElement buildAmbiente(String specId, AmbienteDocDTO dto) {
        AmbienteDocElement ambiente = AmbienteDocElement.fromDto(dto, specId);
        setSyncStatus(ambiente, dto.getDocType());
        return ambiente;
    }

    private ItemDocElement buildItem(String specId, ItemDocDTO dto) {
        ItemType type = catalogSearch.findInCatalog(dto.getTypeId(), ItemType.class);
        ItemDocElement item = ItemDocElement.fromDto(dto, specId, type);
        setSyncStatus(item, dto.getDocType());
        return item;
    }

    private MaterialDocElement buildMaterial(String specId, DocElementDTO dto) {
        MaterialDocElement material = MaterialDocElement.fromDto(dto, specId);
        setSyncStatus(material, dto.getDocType());
        return material;
    }

    private MarcaDocElement buildMarca(String specId, DocElementDTO dto) {
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