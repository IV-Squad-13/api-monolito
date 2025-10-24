package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "especificacao_rev")
@CompoundIndex(name = "especificacao_rev_unique", def = "{'revisaoId' : 1, 'especificacao': 1}", unique = true)
public class EspecificacaoRevDocElement extends RevDocElement {

    @DBRef
    @Field("especificacao")
    private EspecificacaoDoc especificacao;

    private boolean isNameApproved;
    private boolean isDescApproved;
    private boolean isObsApproved;
}