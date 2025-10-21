package com.squad13.apimonolito.util.factory;

import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResponseDocFactory {

    public AggregationOperation lookupLocais(LoadDocumentParamsDTO params) {
        Document itemsLookup = params.isLoadItems()
                ? buildLookup("items", "itemIds", "_id", "items", null)
                : new Document();

        Document ambientesLookup = params.isLoadAmbientes()
                ? buildLookup("ambientes", "ambienteIds", "_id", "ambientes",
                    itemsLookup != null ? List.of(itemsLookup) : null)
                : new Document();

        Document locaisLookup = params.isLoadLocais()
                ? buildLookup("locais", "locaisIds", "_id", "locais",
                    ambientesLookup != null ? List.of(ambientesLookup) : null)
                : new Document()
                    .append("from", "locais")
                    .append("let", new Document("locaisIds", "$locaisIds"))
                    .append("pipeline", List.of())
                    .append("as", "locais");

        return context -> new Document("$lookup", locaisLookup);
    }

    public AggregationOperation lookupMateriais(LoadDocumentParamsDTO params) {
        Document marcasLookup = params.isLoadMarcas()
                ? buildLookup("marcas", "marcaIds", "_id", "marcas", null)
                : new Document();

        Document materiaisLookup = params.isLoadMateriais()
                ? buildLookup("materiais", "materiaisIds", "_id", "materiais", List.of(marcasLookup))
                : new Document();

        return context -> new Document("$lookup", materiaisLookup);
    }

    public Document buildLookup(String from, String localFieldIds, String foreignField,
                                 String asField, List<Document> nestedLookups) {

        List<Document> pipeline = new ArrayList<>();

        pipeline.add(new Document("$match",
                new Document("$expr",
                        new Document("$in", List.of("$_id", "$$" + localFieldIds)))
        ));

        if (nestedLookups != null && !nestedLookups.isEmpty()) {
            pipeline.addAll(
                    nestedLookups.stream()
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