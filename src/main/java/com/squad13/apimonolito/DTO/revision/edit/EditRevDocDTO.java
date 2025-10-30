package com.squad13.apimonolito.DTO.revision.edit;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.squad13.apimonolito.DTO.editor.AmbienteDocDTO;
import com.squad13.apimonolito.DTO.editor.ItemDocDTO;
import com.squad13.apimonolito.DTO.editor.MarcaElementDTO;
import com.squad13.apimonolito.DTO.editor.MaterialElementDTO;
import com.squad13.apimonolito.util.enums.ApprovalEnum;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.enums.RevDocElementEnum;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "docType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EditSpecRevDocDTO.class, name = "ESPECIFICACAO"),
        @JsonSubTypes.Type(value = EditLocalRevDocDTO.class, name = "LOCAL"),
        @JsonSubTypes.Type(value = EditAmbRevDocDTO.class, name = "AMBIENTE"),
        @JsonSubTypes.Type(value = EditItemRevDocDTO.class, name = "ITEM"),
        @JsonSubTypes.Type(value = EditMatRevDocDTO.class, name = "MATERIAL"),
        @JsonSubTypes.Type(value = EditMarRevDocDTO.class, name = "MARCA")
})
public class EditRevDocDTO {

    private Boolean isApproved;

    private ApprovalEnum approvalType;

    private String comment;

    @NotNull(message = "Informe o tipo de documento")
    private RevDocElementEnum docType;
}