package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "material_rev")
@CompoundIndex(name = "material_unique", def = "{'revisaoId' : 1, 'material': 1}", unique = true)
public class MaterialRevDocElement extends RevDocElement {

    @DBRef
    @NotNull
    private MaterialDocElement material;

    @DBRef
    private List<MarcaRevDocElement> marcaRevList;
}