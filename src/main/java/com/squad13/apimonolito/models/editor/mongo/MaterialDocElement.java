package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "materiais")
@CompoundIndex(name = "catalog_name_unique", def = "{'catalogId' : 1, 'name': 1, 'empreendimento': 1}", unique = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MaterialDocElement extends DocElement {

    @DBRef(lazy = true)
    @Field("marcas")
    private List<MarcaDocElement> marcaDocList = new ArrayList<>();
}
