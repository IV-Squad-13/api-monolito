package com.squad13.apimonolito.util.search;

import com.squad13.apimonolito.DTO.editor.res.ResDocElementDTO;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentSearch {

    private final MongoTemplate mongoTemplate;

    public List<MatchOperation> buildMatchOps(Map<String, Object> filters) {
        return filters.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> {
                    Object value = e.getValue();
                    Criteria criteria = (value instanceof String)
                            ? Criteria.where(e.getKey()).regex((String) value, "i")
                            : Criteria.where(e.getKey()).is(value);

                    return Aggregation.match(criteria);
                })
                .toList();
    }


    public <T> T findInDocument(ObjectId id, Class<T> clazz) {
        return Optional.ofNullable(mongoTemplate.findById(id, clazz))
                .orElseThrow(() -> new ResourceNotFoundException(
                        clazz.getSimpleName() + " não encontrado (id=" + id + ")"
                ));
    }

    public <T extends DocElement> List<T> findDocuments(Class<T> clazz) {
        return mongoTemplate.findAll(clazz);
    }

    public <T> List<T> findWithAggregation(String collection, Class<T> resultType, Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, collection, resultType).getMappedResults();
    }

    public <T> List<T> searchWithAggregation(
            String collection,
            Class<T> resultType,
            List<MatchOperation> matchOperations,
            Aggregation aggregation
    ) {
        List<AggregationOperation> operations = new ArrayList<>();
        operations.addAll(matchOperations);
        operations.addAll(aggregation.getPipeline().getOperations());

        Aggregation finalAggregation = Aggregation.newAggregation(operations);
        return mongoTemplate.aggregate(finalAggregation, collection, resultType).getMappedResults();
    }

    public <T> void bulkSave(Class<T> clazz, List<T> docs) {
        if (docs == null || docs.isEmpty()) return;

        mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, clazz)
                .insert(docs)
                .execute();

    }

    @SuppressWarnings("unchecked")
    public <T> void deleteWithReferences(ObjectId id, Class<T> clazz) {
        T target = findInDocument(id, clazz);

        Query referencingQuery = new Query(Criteria.where("prevId").is(id));
        List<DocElement> referencingDocs = mongoTemplate.find(referencingQuery, DocElement.class);

        referencingDocs.forEach(ref -> {
            deleteWithReferences(ref.getId(), (Class<T>) ref.getClass());
        });

        Query mainQuery = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(mainQuery, clazz);
    }
}