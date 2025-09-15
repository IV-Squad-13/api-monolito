package com.squad13.apimonolito.models.editor.mongo;

import com.squad13.apimonolito.models.editor.structures.ElementDoc;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "ambientes")
@CompoundIndex(name = "catalog_name_unique", def = "{'catalogId' : 1, 'name': 1}", unique = true)
public class AmbienteDoc extends ElementDoc {

    @DBRef
    @Field("items")
    private List<ItemDoc> itemDocList;
}
