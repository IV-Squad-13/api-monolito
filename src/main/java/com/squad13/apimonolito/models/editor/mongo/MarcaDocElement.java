package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "marcas")
@CompoundIndex(
        name = "marca_unique",
        def = "{'catalogId' : 1, 'name': 1, 'parentId': 1, 'especificacaoId': 1}",
        unique = true
)
public class MarcaDocElement extends DocElement {

    public static MarcaDocElement fromDto(String especId, DocElementDTO dto) {
        return DocElement.genericFromDto(dto, especId, MarcaDocElement.class);
    }
}