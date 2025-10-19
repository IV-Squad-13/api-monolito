package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "especificacoes")
@CompoundIndex(
        name = "especificacao_unique",
        def = "{'empreendimentoId' : 1, 'name': 1}",
        unique = true
)
public class EspecificacaoDoc {

    @MongoId(FieldType.OBJECT_ID)
    private ObjectId id;

    @NotNull
    @Indexed
    private Long empreendimentoId;

    @NotBlank
    @Size(max = 40)
    private String name;

    @Size(max = 160)
    private String desc;

    @Size(max = 160)
    private String obs;

    @Field("locaisIds")
    @JsonProperty("locais")
    private List<ObjectId> locaisIds = new ArrayList<>();

    @Field("materiaisIds")
    @JsonProperty("materiais")
    private List<ObjectId> materiaisIds = new ArrayList<>();

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant created;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updated;
}