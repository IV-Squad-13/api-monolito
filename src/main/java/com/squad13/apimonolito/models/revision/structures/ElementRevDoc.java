package com.squad13.apimonolito.models.revision.structures;

import com.squad13.apimonolito.models.editor.structures.ElementDoc;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.Instant;

@Getter
@Setter
public abstract class ElementRevDoc {

    @Id
    private String id;

    @NotNull
    private Long revisaoId;

    private boolean isApproved;

    private String comment;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;
}