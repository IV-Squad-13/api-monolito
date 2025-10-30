package com.squad13.apimonolito.util.factory;

import com.squad13.apimonolito.DTO.revision.LoadRevDocParamsDTO;
import com.squad13.apimonolito.util.builder.LookupBuilder;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RevResponseDocFactory {

    public AggregationOperation lookupLocais(LoadRevDocParamsDTO params) {
        Document lookup = buildConditionalLookup(
                (params.isLoadLocais() || params.isLoadRevDocuments()),
                "local_rev", "localRevIds", "localRevs",
                params.isLoadRevDocuments() ? buildAmbientesLookup(params) : null
        );
        return toAggregationOperation(lookup);
    }

    public AggregationOperation lookupMateriais(LoadRevDocParamsDTO params) {
        Document lookup = buildConditionalLookup(
                (params.isLoadMateriais() || params.isLoadRevDocuments()),
                "material_rev", "materialRevIds", "materialRevs",
                params.isLoadRevDocuments() ? buildMarcasLookup() : null
        );
        return toAggregationOperation(lookup);
    }
    public AggregationOperation lookupAmbientes(LoadRevDocParamsDTO params) {
        Document lookup = buildConditionalLookup(
                (params.isLoadAmbientes() || params.isLoadRevDocuments()),
                "ambiente_rev", "ambienteRevIds", "ambienteRevs",
                params.isLoadRevDocuments() ? buildItemsLookup() : null
        );
        return toAggregationOperation(lookup);
    }

    public AggregationOperation lookupItems(LoadRevDocParamsDTO params) {
        Document lookup = buildConditionalLookup(
                (params.isLoadItems() || params.isLoadRevDocuments()),
                "items_rev", "itemRevIds", "itemRevs",
                null
        );
        return toAggregationOperation(lookup);
    }

    public AggregationOperation lookupMarcas(LoadRevDocParamsDTO params) {
        Document lookup = buildConditionalLookup(
                (params.isLoadMarcas() || params.isLoadRevDocuments()),
                "marcas_rev", "marcaRevIds", "marcaRevs",
                null
        );
        return toAggregationOperation(lookup);
    }

    private Document buildAmbientesLookup(LoadRevDocParamsDTO params) {
        Document itemsLookup = params.isLoadRevDocuments()
                ? buildItemsLookup()
                : null;

        return LookupBuilder.getDocument("ambiente_rev", "ambienteRevIds", "_id", "ambienteRevs",
                itemsLookup != null ? List.of(itemsLookup) : null);
    }

    private Document buildItemsLookup() {
        return LookupBuilder.getDocument("items_rev", "itemRevIds", "_id", "itemRevs", null);
    }

    private Document buildMarcasLookup() {
        return LookupBuilder.getDocument("marcas_rev", "marcaRevIds", "_id", "marcaRevs", null);
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