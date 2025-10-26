package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "ambiente_rev")
@CompoundIndex(name = "ambiente_rev_unique", def = "{'revisionId' : 1, 'ambienteDocId' : 1}", unique = true)
public class AmbienteRevDocElement extends RevDocElement {

    @Indexed
    @NotNull
    private ObjectId ambienteDocId;
}