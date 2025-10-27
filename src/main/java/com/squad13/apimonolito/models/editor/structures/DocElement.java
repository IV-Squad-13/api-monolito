package com.squad13.apimonolito.models.editor.structures;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.function.Supplier;

@Data
@MappedSuperclass
public abstract class DocElement {

    @MongoId(FieldType.OBJECT_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @NotNull
    @Indexed
    private Long catalogId;

    @NotNull
    @Indexed
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId especificacaoId;

    @Indexed
    @JsonSerialize(using = ToStringSerializer.class)
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
    T fromDto(D dto, ObjectId especificacaoId, Supplier<T> factory) {
        T instance = factory.get();

        instance.setId(ObjectId.get());
        instance.setName(dto.getName());
        instance.setEspecificacaoId(especificacaoId);

        if (dto.getCatalogId() != null) {
            instance.setCatalogId(dto.getCatalogId());
        }
        if (dto.getParentId() != null) {
            instance.setParentId(ObjectId.get());
        }

        return instance;
    }
}