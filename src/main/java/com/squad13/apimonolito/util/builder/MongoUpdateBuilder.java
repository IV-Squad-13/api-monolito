package com.squad13.apimonolito.util.builder;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MongoUpdateBuilder {

    private final MongoTemplate mongoTemplate;

    public Map<String, Object> toMap(Object source) {
        Document bson = new Document();
        mongoTemplate.getConverter().write(source, bson);
        return bson;
    }
}