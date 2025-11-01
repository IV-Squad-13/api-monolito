package com.squad13.apimonolito.DTO.editor;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public record BulkDocElementCatalogCreationDTO(
        @NotEmpty
        List<DocElementCatalogCreationDTO> elements
) { }