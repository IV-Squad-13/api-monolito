package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "locais")
@CompoundIndex(name = "local_unique", def = "{'local': 1, 'empreendimento': 1}", unique = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class LocalDocElement extends DocElement {

    @Transient
    @JsonProperty
    private Long catalogId;

    @Transient
    @JsonProperty
    private String name;

    @Transient
    @JsonProperty
    private boolean inSync;

    private LocalEnum local;

    @DBRef(lazy = true)
    @Field("ambientes")
    private List<AmbienteDocElement> ambienteDocList = new ArrayList<>();
}
