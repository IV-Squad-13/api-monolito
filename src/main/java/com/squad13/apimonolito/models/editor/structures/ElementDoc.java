package com.squad13.apimonolito.models.editor.structures;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.Instant;

@Getter
@Setter
public abstract class ElementDoc {

    @Id
    private String id;

    @NotNull
    private Long catalogId;

    @NotNull
    @DBRef
    private EspecificacaoDoc especificacaoDoc;

    @NotBlank
    private String name;

    private boolean inSync;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;
}
