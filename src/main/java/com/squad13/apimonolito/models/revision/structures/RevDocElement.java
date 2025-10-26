package com.squad13.apimonolito.models.revision.structures;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
public abstract class RevDocElement {

    @Id
    private String id;

    @NotNull
    private Long revisionId;

    private boolean isApproved;

    private String comment;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;
}