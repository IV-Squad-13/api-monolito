package com.squad13.apimonolito.models.editor.structures;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public abstract class DocElement {

    @Id
    private String id;

    @NotNull
    private Long catalogId;

    @NotBlank
    private String name;

    private boolean inSync;

    private List<DocElement> docElementList;
}
