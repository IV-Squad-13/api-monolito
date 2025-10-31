package com.squad13.apimonolito.util.builder;


import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Component
public class LookupBuilder {

    public static Document getDocument(String from, String localFieldIds, String foreignField, String asField, List<Document> nestedLookups) {
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