package com.squad13.apimonolito.models.editor.structures;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

public abstract class ElementDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
