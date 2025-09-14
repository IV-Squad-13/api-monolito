package com.squad13.apimonolito.models.editor.mongo;

import com.squad13.apimonolito.models.editor.structures.ElementDoc;
import com.squad13.apimonolito.util.annotations.MongoEntityType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@MongoEntityType("AMBIENTE")
@EqualsAndHashCode(callSuper = false)
@Document(collection = "ambientes")
public class AmbienteDoc extends ElementDoc {

    @DBRef
    @Field("items")
    private List<ItemDoc> items;
}
