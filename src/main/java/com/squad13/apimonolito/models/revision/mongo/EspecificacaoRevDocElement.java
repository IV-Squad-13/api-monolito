package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "especificacao_rev")
@CompoundIndex(name = "especificacao_rev_unique", def = "{'revisionId' : 1, 'especificacaoDocId': 1}", unique = true)
public class EspecificacaoRevDocElement extends RevDocElement {

    @Indexed
    @NotNull
    private ObjectId especificacaoDocId;

    private boolean isNameApproved;
    private boolean isDescApproved;
    private boolean isObsApproved;
}