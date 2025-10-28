package com.squad13.apimonolito.util.factory;

import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.revision.LoadRevDocParamsDTO;
import com.squad13.apimonolito.models.revision.mongo.LocalRevDocElement;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class RevResponseDocFactory {

    public AggregationOperation lookupLocais(LoadRevDocParamsDTO params) {
        Document lookup = buildConditionalLookup(
                params.isLoadRevDocuments(),
                "local_rev", "localRevIds", "localRevs",
                params.isLoadRevDocuments() ? buildAmbientesLookup(params) : null
        );
        return toAggregationOperation(lookup);
    }

    public AggregationOperation lookupMateriais(LoadRevDocParamsDTO params) {
        Document lookup = buildConditionalLookup(
                params.isLoadRevDocuments(),
                "material_rev", "materialRevIds", "materialRevs",
                params.isLoadRevDocuments() ? buildMarcasLookup() : null
        );
        return toAggregationOperation(lookup);
    }

    public AggregationOperation lookupMarcas(LoadRevDocParamsDTO params) {
        Document lookup = buildConditionalLookup(
                params.isLoadRevDocuments(),
                "marcas_rev", "marcaRevIds", "marcaRevs",
                null
        );
        return toAggregationOperation(lookup);
    }

    private Document buildAmbientesLookup(LoadRevDocParamsDTO params) {
        Document itemsLookup = params.isLoadRevDocuments()
                ? buildItemsLookup()
                : null;

        return buildLookup("ambiente_rev", "ambienteRevIds", "_id", "ambienteRevs",
                itemsLookup != null ? List.of(itemsLookup) : null);
    }
    private Document buildItemsLookup() {
        return buildLookup("items_rev", "itemRevIds", "_id", "itemRevs", null);
    }

    private Document buildMarcasLookup() {
        return buildLookup("marcas_rev", "marcaRevIds", "_id", "marcaRevs", null);
    }

    private Document buildConditionalLookup(
            boolean condition,
            String from,
            String localFieldIds,
            String asField,
            Document nestedLookup
    ) {
        if (!condition) return null;
        return buildLookup(from, localFieldIds, "_id", asField,
                nestedLookup != null ? List.of(nestedLookup) : null);
    }

    private AggregationOperation toAggregationOperation(Document lookup) {
        return context -> lookup == null
                ? new Document("$match", new Document())
                : new Document("$lookup", lookup);
    }

    public Document buildLookup(
            String from,
            String localFieldIds,
            String foreignField,
            String asField,
            List<Document> nestedLookups
    ) {
        return PipelineBuilder.getDocument(from, localFieldIds, foreignField, asField, nestedLookups);
    }
}