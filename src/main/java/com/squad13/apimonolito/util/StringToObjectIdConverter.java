package com.squad13.apimonolito.util;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToObjectIdConverter implements Converter<String, ObjectId> {
    @Override
    public ObjectId convert(String source) {
        if (source.isBlank()) return null;
        return new ObjectId(source);
    }
}
