package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

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

    public static MarcaDocElement fromDto(ObjectId especId, DocElementDTO dto) {
        return DocElement.fromDto(dto, especId, MarcaDocElement::new);
    }
}