package com.squad13.apimonolito.util.factory;

import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.util.builder.LookupBuilder;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResponseDocFactory {

    public AggregationOperation lookupLocais(LoadDocumentParamsDTO params) {
        Document lookup = buildConditionalLookup(
                params.isLoadLocais(),
                "locais", "locaisIds", "locais",
                params.isLoadAmbientes() ? buildAmbientesLookup(params) : null
        );
        return toAggregationOperation(lookup);
    }

    public AggregationOperation lookupAmbientes(LoadDocumentParamsDTO params) {
        Document lookup = buildConditionalLookup(
                params.isLoadAmbientes(),
                "ambientes", "ambienteIds", "ambientes",
                params.isLoadItems() ? buildItemsLookup(params) : null
        );
        return toAggregationOperation(lookup);
    }

    public AggregationOperation lookupItems(LoadDocumentParamsDTO params) {
        Document lookup = buildConditionalLookup(
                params.isLoadItems(),
                "items", "itemIds", "items",
                null
        );
        return toAggregationOperation(lookup);
    }

    public AggregationOperation lookupMateriais(LoadDocumentParamsDTO params) {
        Document lookup = buildConditionalLookup(
                params.isLoadMateriais(),
                "materiais", "materiaisIds", "materiais",
                params.isLoadMarcas() ? buildMarcasLookup(params) : null
        );
        return toAggregationOperation(lookup);
    }

    public AggregationOperation lookupMarcas(LoadDocumentParamsDTO params) {
        Document lookup = buildConditionalLookup(
                params.isLoadMarcas(),
                "marcas", "marcaIds", "marcas",
                null
        );
        return toAggregationOperation(lookup);
    }

    private Document buildAmbientesLookup(LoadDocumentParamsDTO params) {
        Document itemsLookup = params.isLoadItems()
                ? buildItemsLookup(params)
                : null;

        return LookupBuilder.getDocument("ambientes", "ambienteIds", "_id", "ambientes",
                itemsLookup != null ? List.of(itemsLookup) : null);
    }
    private Document buildItemsLookup(LoadDocumentParamsDTO params) {
        return LookupBuilder.getDocument("items", "itemIds", "_id", "items", null);
    }

    private Document buildMarcasLookup(LoadDocumentParamsDTO params) {
        return LookupBuilder.getDocument("marcas", "marcaIds", "_id", "marcas", null);
    }

    private Document buildConditionalLookup(
            boolean condition,
            String from,
            String localFieldIds,
            String asField,
            Document nestedLookup
    ) {
        if (!condition) return null;
        return LookupBuilder.getDocument(from, localFieldIds, "_id", asField,
                nestedLookup != null ? List.of(nestedLookup) : null);
    }

    private AggregationOperation toAggregationOperation(Document lookup) {
        return context -> lookup == null
                ? new Document("$match", new Document())
                : new Document("$lookup", lookup);
    }
}