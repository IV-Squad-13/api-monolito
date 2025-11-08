package com.squad13.apimonolito.DTO.editor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.util.ObjectIdDeserializer;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;

public record DocElementCatalogCreationDTO(

        @NotNull(message = "Informe um tipo válido para o documento")
        DocElementEnum type,

        @NotNull(message = "Informe o ID do elemento no catálogo")
        Long elementId,

        ObjectId parentId
) { }