package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "especificacao_rev")
@CompoundIndex(name = "especificacao_rev_unique", def = "{'revisionId' : 1, 'especificacaoDocId': 1}", unique = true)
public class EspecificacaoRevDocElement extends RevDocElement {

    @Transient
    private EspecificacaoDoc revisedDoc;

    @Indexed
    private List<ObjectId> localRevIds = new ArrayList<>();

    @Transient
    private List<LocalRevDocElement> localRevs = new ArrayList<>();

    @Indexed
    private List<ObjectId> materialRevIds = new ArrayList<>();

    @Transient
    private List<MaterialRevDocElement> materialRevs = new ArrayList<>();

    private Boolean isNameApproved;
    private Boolean isDescApproved;
    private Boolean isObsApproved;
}