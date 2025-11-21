package com.squad13.apimonolito.util.search;

import com.squad13.apimonolito.exceptions.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class DocumentSearch {

    private final MongoTemplate mongoTemplate;

    public String getCollection(DocElementEnum docType) {
        Document doc = docType.getDocElement().getAnnotation(Document.class);
        if (doc == null || doc.collection().isBlank()) {
            return docType.getDocElement().getSimpleName();
        }

        return doc.collection();
    }

    public List<MatchOperation> buildMatchOps(Map<String, Object> filters) {
        return filters.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> {
                    Object value = e.getValue();
                    Criteria criteria = (value instanceof String strValue)
                            ? Criteria.where(e.getKey()).regex(strValue, "i")
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

    public <T> List<T> findAllByIds(List<ObjectId> ids, Class<T> clazz) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        Query query = new Query(Criteria.where("_id").in(ids));
        return mongoTemplate.find(query, clazz);
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

    public <T> void bulkSave(Class<? extends T> clazz, List<? extends T> docs) {
        if (docs == null || docs.isEmpty() || clazz == null) return;

        mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, clazz)
                .insert(docs)
                .execute();
    }

    public <T> void bulkUpdate(List<ObjectId> ids, Update update, Class<T> clazz) {
        if (ids == null || ids.isEmpty() || update == null || clazz == null) return;

        Query query = new Query(Criteria.where("_id").in(ids));
        mongoTemplate.updateMulti(query, update, clazz);
    }

    public <S extends DocElement> ObjectId upsert(S source, Class<? extends S> sourceClass) {
        if (source == null || sourceClass == null) return null;

        Query query = new Query();

        for (String key : source.getUniqueKeys()) {
            Object value = new BeanWrapperImpl(source).getPropertyValue(key);
            query.addCriteria(Criteria.where(key).is(value));
        }

        Update update = new Update();
        MongoConverter converter = mongoTemplate.getConverter();

        org.bson.Document doc = new org.bson.Document();
        converter.write(source, doc);
        doc.forEach(update::set);

        FindAndModifyOptions options = new FindAndModifyOptions()
                .upsert(true)
                .returnNew(true);

        S result = mongoTemplate.findAndModify(query, update, options, sourceClass);

        if (result == null) {
            throw new IllegalStateException("Upsert failed for " + sourceClass.getSimpleName());
        }

        return result.getId();
    }

    @SuppressWarnings("unchecked")
    public <T> void deleteWithReferences(ObjectId id, Class<T> clazz) {
        Query referencingQuery = new Query(Criteria.where("prevId").is(id));
        List<DocElement> referencingDocs = mongoTemplate.find(referencingQuery, DocElement.class);

        referencingDocs.forEach(ref -> {
            deleteWithReferences(ref.getId(), (Class<T>) ref.getClass());
        });

        Query mainQuery = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(mainQuery, clazz);
    }

    public <T extends RevDocElement> void deleteByRevisionId(Long revisionId, Class<T> clazz) {
        Query revisionQuery = new Query(Criteria.where("revisionId").is(revisionId));
        mongoTemplate.remove(revisionQuery, clazz);
    }
}