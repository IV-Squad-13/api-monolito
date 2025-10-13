package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.util.enums.LocalEnum;
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
@Document(collection = "ambientes")
@CompoundIndex(name = "ambiente_unique", def = "{'catalogId' : 1, 'name': 1, 'empreendimento': 1}", unique = true)
public class AmbienteDocElement extends DocElement {

    private LocalEnum local;

    @DBRef(lazy = true)
    @Field("items")
    private List<ItemDocElement> itemDocList = new ArrayList<>();
}
