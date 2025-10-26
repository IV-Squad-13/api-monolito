package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.squad13.apimonolito.DTO.editor.AmbienteDocDTO;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "ambientes")
@CompoundIndex(
        name = "ambiente_unique",
        def = "{'catalogId' : 1, 'name': 1, 'local': 1, 'especificacaoId': 1}",
        unique = true
)
public class AmbienteDocElement extends DocElement {

    private LocalEnum local;

    @Indexed
    private List<ObjectId> itemIds = new ArrayList<>();

    @Transient
    private List<ItemDocElement> items = new ArrayList<>();

    public static AmbienteDocElement fromDto(AmbienteDocDTO dto, ObjectId especificacaoId) {
        AmbienteDocElement ambiente = DocElement.genericFromDto(dto, especificacaoId, AmbienteDocElement.class);
        ambiente.setLocal(dto.getLocal());
        return ambiente;
    }
}