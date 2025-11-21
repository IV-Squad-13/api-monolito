package com.squad13.apimonolito.DTO.editor.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.function.Supplier;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResDocElementDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private Long catalogId;
    private String name;
    private boolean inSync;

    public void populateExtraFields(DocElement doc) { }

    public static <D extends DocElement, T extends ResDocElementDTO>
    T fromDoc(D doc, Supplier<T> factory) {
        T instance = factory.get();
        instance.setId(doc.getId());
        instance.setCatalogId(doc.getCatalogId());
        instance.setName(doc.getName());
        instance.setInSync(doc.isInSync());

        instance.populateExtraFields(doc);

        return instance;
    }
}