package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
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
@Document(collection = "local_rev")
@CompoundIndex(name = "local_rev_unique", def = "{'revisionId' : 1, 'revisedDocId': 1}", unique = true)
public class LocalRevDocElement extends RevDocElement {

    @Transient
    private LocalDoc doc;

    @Indexed
    private List<ObjectId> ambienteRevIds = new ArrayList<>();

    @Transient
    private List<AmbienteRevDocElement> ambienteRevs = new ArrayList<>();

}