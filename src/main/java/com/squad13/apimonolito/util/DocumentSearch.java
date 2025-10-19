package com.squad13.apimonolito.util;

import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentSearch {

    private final MongoTemplate mongoTemplate;

    public <T> T findInDocument(ObjectId id, Class<T> clazz) {
        return Optional.ofNullable(mongoTemplate.findById(id, clazz))
                .orElseThrow(() -> new ResourceNotFoundException(
                        clazz.getSimpleName() + " não encontrado (id=" + id + ")"
                ));
    }

    public <T extends DocElement> List<T> findDocuments(Class<T> clazz) {
        return mongoTemplate.findAll(clazz);
    }

    public <T> List<T> findWithAggregation(String collectionName, Class<T> resultClass, AggregationOperation... operations) {
        Aggregation aggregation = Aggregation.newAggregation(operations);

        return mongoTemplate.aggregate(aggregation, collectionName, resultClass)
                .getMappedResults();
    }

    public <T> T findOneWithAggregation(String collectionName, Class<T> resultClass, AggregationOperation... operations) {
        List<T> results = findWithAggregation(collectionName, resultClass, operations);
        return results.isEmpty() ? null : results.getFirst();
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