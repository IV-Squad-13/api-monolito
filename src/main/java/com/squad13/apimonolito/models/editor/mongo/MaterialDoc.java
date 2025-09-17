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
@Document(collection = "materiais")
@CompoundIndex(name = "catalog_name_unique", def = "{'catalogId' : 1, 'name': 1, 'empreendimento': 1}", unique = true)
public class MaterialDoc extends ElementDoc {

    @DBRef
    @Field("marcas")
    private List<MarcaDoc> marcaDocList;
}
