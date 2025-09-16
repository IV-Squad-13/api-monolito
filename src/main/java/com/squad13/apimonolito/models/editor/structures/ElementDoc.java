package com.squad13.apimonolito.models.editor.structures;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
public abstract class ElementDoc {

    @Id
    private String id;

    @NotNull
    private Long catalogId;

    @NotBlank
    private String name;

    private boolean inSync;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;
}
