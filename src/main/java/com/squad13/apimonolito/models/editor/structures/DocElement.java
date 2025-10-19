package com.squad13.apimonolito.models.editor.structures;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Data
@MappedSuperclass
public abstract class DocElement {

    @MongoId(FieldType.OBJECT_ID)
    private ObjectId id;

    @NotNull
    @Indexed
    private Long catalogId;

    @NotNull
    @Indexed
    private ObjectId especificacaoId;

    @Indexed
    private ObjectId parentId;

    @NotBlank
    @Size(max = 100)
    private String name;

    private boolean inSync;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant created;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updated;

    public static <D extends DocElementDTO, T extends DocElement>
    T genericFromDto(D dto, ObjectId especificacaoId, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            instance.setId(ObjectId.get());
            instance.setName(dto.getName());
            instance.setCatalogId(dto.getCatalogId());
            instance.setParentId(dto.getParentId());
            instance.setEspecificacaoId(especificacaoId);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar instância de " + clazz.getSimpleName(), e);
        }
    }
}