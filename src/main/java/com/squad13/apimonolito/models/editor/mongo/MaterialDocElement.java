package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "materiais")
@CompoundIndex(
        name = "material_unique",
        def = "{'catalogId' : 1, 'name': 1, 'especificacaoId': 1}",
        unique = true
)
public class MaterialDocElement extends DocElement {

    @Transient
    private ObjectId parentId;

    @Indexed
    private List<ObjectId> marcaIds = new ArrayList<>();

    @Transient
    private List<MarcaDocElement> marcas = new ArrayList<>();

    public static MaterialDocElement fromDto(DocElementDTO dto, ObjectId especId) {
        return DocElement.genericFromDto(dto, especId, MaterialDocElement.class);
    }
}
