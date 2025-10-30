package com.squad13.apimonolito.DTO.editor.res;

import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Supplier;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResDocElementDTO {
    private String id;
    private Long catalogId;
    private String name;
    private boolean inSync;

    public static <D extends DocElement, T extends ResDocElementDTO>
    T fromDoc(D doc, Supplier<T> factory) {
        T instance = factory.get();
        instance.setId(doc.getId().toHexString());
        instance.setCatalogId(doc.getCatalogId());
        instance.setName(doc.getName());
        instance.setInSync(doc.isInSync());
        return instance;
    }
}