package com.squad13.apimonolito.util.factory;

import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                ? buildLookup("items", "itemIds", "_id", "items", null)
                : null;

        return buildLookup("ambientes", "ambienteIds", "_id", "ambientes",
                itemsLookup != null ? List.of(itemsLookup) : null);
    }

    private Document buildMarcasLookup(LoadDocumentParamsDTO params) {
        return buildLookup("marcas", "marcaIds", "_id", "marcas", null);
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
        List<Document> pipeline = new ArrayList<>();

        pipeline.add(new Document("$match",
                new Document("$expr",
                        new Document("$in", List.of(
                                "$" + foreignField,
                                new Document("$ifNull", List.of("$$" + localFieldIds, List.of()))
                        ))
                )
        ));

        if (nestedLookups != null && !nestedLookups.isEmpty()) {
            pipeline.addAll(
                    nestedLookups.stream()
                            .filter(Objects::nonNull)
                            .map(l -> new Document("$lookup", l))
                            .toList()
            );
        }

        return new Document()
                .append("from", from)
                .append("let", new Document(localFieldIds, "$" + localFieldIds))
                .append("pipeline", pipeline)
                .append("as", asField);
    }
}