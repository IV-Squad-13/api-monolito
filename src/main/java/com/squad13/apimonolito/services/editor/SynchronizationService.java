package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.search.CatalogSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SynchronizationService {

    private final CatalogSearch catalogSearch;

    public boolean inSync(DocElement doc, DocElementEnum type) {
        Object catalogEntity = catalogSearch.findInCatalog(doc.getCatalogId(), type.getCatalogEntity());

        if (catalogEntity == null) {
            return false;
        }

        return switch (type) {
            case AMBIENTE -> isAmbienteInSync((Ambiente) catalogEntity, (AmbienteDocElement) doc);
            case ITEM -> isItemInSync((ItemDesc) catalogEntity, (ItemDocElement) doc);
            case MATERIAL -> isMaterialInSync((Material) catalogEntity, (MaterialDocElement) doc);
            case MARCA -> isMarcaInSync((Marca) catalogEntity, (MarcaDocElement) doc);
        };
    }

    private boolean isAmbienteInSync(Ambiente catalog, AmbienteDocElement doc) {
        return catalog.getName().equalsIgnoreCase(doc.getName())
                && catalog.getLocal() == doc.getLocal();
    }

    private boolean isItemInSync(ItemDesc catalog, ItemDocElement doc) {
        return catalog.getName().equalsIgnoreCase(doc.getName())
                && Objects.equals(catalog.getDesc(), doc.getDesc())
                && Objects.equals(catalog.getType().getId(), doc.getTypeId())
                && catalog.getType().getName().equalsIgnoreCase(doc.getType());
    }

    private boolean isMaterialInSync(Material catalog, MaterialDocElement doc) {
        return catalog.getName().equalsIgnoreCase(doc.getName());
    }

    private boolean isMarcaInSync(Marca catalog, MarcaDocElement doc) {
        return catalog.getName().equalsIgnoreCase(doc.getName());
    }
}