package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "especificacoes")
@CompoundIndex(name = "catalog_name_unique", def = "{'empreendimentoId' : 1, 'name': 1}", unique = true)
public class EspecificacaoDoc {

    @Id
    @JsonProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @NotNull
    private Long empreendimentoId;

    @NotBlank
    @Size(max = 40)
    private String name;

    @Size(max = 160)
    private String desc;

    @Size(max = 160)
    private String obs;

    @DBRef(lazy = true)
    @JsonManagedReference
    private List<LocalDocElement> locais = new ArrayList<>();

    @DBRef(lazy = true)
    private List<MaterialDocElement> materiais = new ArrayList<>();

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "America/Aracaju")
    private Instant created;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "America/Aracaju")
    private Instant updated;
}
