package com.squad13.apimonolito.models.editor.mongo;

import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "locais")
public class LocalDoc extends DocElement {

    @Transient
    private Long catalogId;

    @Field("name")
    private LocalEnum name;

    @DBRef
    @Field("ambientes")
    private List<AmbienteDoc> ambienteList;
}
