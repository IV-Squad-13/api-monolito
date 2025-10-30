package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
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
@EqualsAndHashCode(callSuper = false)
@Document(collection = "material_rev")
@CompoundIndex(name = "material_unique", def = "{'revisionId' : 1, 'revisedDocId': 1}", unique = true)
public class MaterialRevDocElement extends RevDocElement {

    @Transient
    private MaterialDocElement doc;

    @Indexed
    private List<ObjectId> marcaRevIds = new ArrayList<>();

    @Transient
    private List<MarcaRevDocElement> marcaRevs = new ArrayList<>();
}