package com.squad13.apimonolito.models.editor.structures;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.Instant;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public abstract class DocElement {

    @Id
    @JsonProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @NotNull
    private Long catalogId;

    @Transient
    private String prevId;

    @NotNull
    @DBRef(lazy = true)
    @JsonIgnore
    private EspecificacaoDoc especificacaoDoc;

    @NotBlank
    private String name;

    private boolean inSync;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "America/Brasilia")
    private Instant created;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "America/Brasilia")
    private Instant updated;

    public static <D extends DocElementDTO, T extends DocElement> T genericFromDto(D dto, EspecificacaoDoc espec, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            instance.setName(dto.getName());
            instance.setCatalogId(dto.getCatalogId());
            instance.setPrevId(dto.getPrevId());
            instance.setEspecificacaoDoc(espec);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar instância de " + clazz.getSimpleName(), e);
        }
    }
}
