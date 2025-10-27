package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.editor.structures.DocElement;
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
@Document(collection = "ambiente_rev")
@CompoundIndex(name = "ambiente_rev_unique", def = "{'revisionId' : 1, 'revisedDocId' : 1}", unique = true)
public class AmbienteRevDocElement extends RevDocElement {

    @Transient
    private AmbienteDocElement doc;

    @Indexed
    private List<ObjectId> itemRevIds = new ArrayList<>();

    @Transient
    private List<ItemRevDocElement> itemRevs = new ArrayList<>();
}