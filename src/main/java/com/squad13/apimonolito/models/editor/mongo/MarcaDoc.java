package com.squad13.apimonolito.models.editor.mongo;

import com.squad13.apimonolito.models.editor.structures.ElementDoc;
import com.squad13.apimonolito.util.annotations.MongoEntityType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@MongoEntityType("MARCA")
@Document(collection = "marcas")
public class MarcaDoc extends ElementDoc {

    @Transient
    private List<ElementDoc> elementDocList;
}