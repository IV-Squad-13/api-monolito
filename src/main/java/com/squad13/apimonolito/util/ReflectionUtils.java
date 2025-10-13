package com.squad13.apimonolito.util;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access field '" + fieldName + "' in " + obj.getClass().getSimpleName(), e);
        }
    }
}