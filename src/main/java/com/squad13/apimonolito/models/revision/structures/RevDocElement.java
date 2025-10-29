package com.squad13.apimonolito.models.revision.structures;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.DTO.editor.res.ResDocElementDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDocDTO;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.function.Supplier;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class RevDocElement {

    @MongoId(FieldType.OBJECT_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @Indexed
    @NotNull
    private ObjectId revisedDocId;

    @NotNull
    private Long revisionId;

    private Boolean isApproved;

    private String comment;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant created;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updated;
}