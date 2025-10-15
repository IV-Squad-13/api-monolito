package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
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
@CompoundIndex(name = "local_unique", def = "{'local': 1, 'especificacaoDoc': 1}", unique = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class LocalDoc {

    @Id
    @JsonProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @NotNull
    @DBRef(lazy = true)
    @JsonIgnore
    private EspecificacaoDoc especificacaoDoc;

    private LocalEnum local;

    @DBRef(lazy = true)
    @Field("ambientes")
    private List<AmbienteDocElement> ambienteDocList = new ArrayList<>();
}
