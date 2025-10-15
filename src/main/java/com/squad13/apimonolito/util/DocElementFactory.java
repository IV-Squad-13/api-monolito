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

@Component
@RequiredArgsConstructor
public class DocElementFactory {

    private final CatalogSearch catalogSearch;
    private final DocumentSearch docSearch;
    private final SynchronizationService syncService;

    public DocElement create(EspecificacaoDoc espec, DocElementDTO dto) {
        return switch (dto.getDocType()) {
            case AMBIENTE -> buildAmbiente(espec, (AmbienteDocDTO) dto);
            case ITEM -> buildItem(espec, (ItemDocDTO) dto);
            case MATERIAL -> buildMaterial(espec, dto);
            case MARCA -> buildMarca(espec, dto);
            default -> throw new InvalidDocumentTypeException("Tipo de elemento não suportado: " + dto.getDocType());
        };
    }

    private AmbienteDocElement buildAmbiente(EspecificacaoDoc espec, AmbienteDocDTO dto) {
        AmbienteDocElement ambiente = AmbienteDocElement.fromDto(dto, espec);
        setSyncStatus(ambiente, dto.getDocType());
        return ambiente;
    }

    private ItemDocElement buildItem(EspecificacaoDoc espec, ItemDocDTO dto) {
        ItemType type = catalogSearch.findInCatalog(dto.getTypeId(), ItemType.class);
        ItemDocElement item = ItemDocElement.fromDto(dto, espec, type);
        setSyncStatus(item, dto.getDocType());
        return item;
    }

    private MaterialDocElement buildMaterial(EspecificacaoDoc espec, DocElementDTO dto) {
        MaterialDocElement material = MaterialDocElement.fromDto(dto, espec);
        setSyncStatus(material, dto.getDocType());
        return material;
    }

    private MarcaDocElement buildMarca(EspecificacaoDoc espec, DocElementDTO dto) {
        MarcaDocElement marca = MarcaDocElement.fromDto(espec, dto);
        setSyncStatus(marca, dto.getDocType());
        return marca;
    }

    private void setSyncStatus(DocElement element, DocElementEnum type) {
        if (element.getCatalogId() != null) {
            element.setInSync(syncService.inSync(element, type));
        }
    }
}