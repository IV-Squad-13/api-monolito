package com.squad13.apimonolito.DTO.editor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "docType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AmbienteDocDTO.class, name = "AMBIENTE"),
        @JsonSubTypes.Type(value = ItemDocDTO.class, name = "ITEM"),
        @JsonSubTypes.Type(value = MaterialElementDTO.class, name = "MATERIAL"),
        @JsonSubTypes.Type(value = MarcaElementDTO.class, name = "MARCA")
})
public abstract class DocElementDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private Long catalogId;

    private String parentId;

    private DocElementEnum docType;
}