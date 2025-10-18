package com.squad13.apimonolito.util;

import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentSearch {

    private final MongoTemplate mongoTemplate;

    public <T> T findInDocument(String id, Class<T> clazz) {
        return Optional.ofNullable(mongoTemplate.findById(id, clazz))
                .orElseThrow(() -> new ResourceNotFoundException(
                        clazz.getSimpleName() + " não encontrado (id=" + id + ")"
                ));
    }

    public <T extends DocElement> List<T> findDocuments(Class<T> clazz) {
        return mongoTemplate.findAll(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends DocElement> void deleteWithReferences(String id, Class<T> clazz) {
        T target = findInDocument(id, clazz);

        Query referencingQuery = new Query(Criteria.where("prevId").is(id));
        List<DocElement> referencingDocs = mongoTemplate.find(referencingQuery, DocElement.class);

        for (DocElement ref : referencingDocs) {
            deleteWithReferences(ref.getId(), (Class<T>) ref.getClass());
        }

        Query mainQuery = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(mainQuery, clazz);
    }
}