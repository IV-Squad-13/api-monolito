package com.squad13.apimonolito.models.editor.mongo;

import com.squad13.apimonolito.models.editor.structures.ElementDoc;
import com.squad13.apimonolito.util.annotations.MongoEntityType;
import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@MongoEntityType("LOCAL")
@Document(collection = "locais")
public class LocalDoc extends ElementDoc {

    @Transient
    private Long catalogId;

    @Field("name")
    private LocalEnum name;

    @DBRef
    @Field("ambientes")
    private List<AmbienteDoc> ambienteList;
}
