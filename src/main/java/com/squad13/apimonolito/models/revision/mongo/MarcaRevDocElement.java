package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "marcas_rev")
@CompoundIndex(name = "catalog_name_unique", def = "{'revisaoId' : 1, 'marca': 1}", unique = true)
public class MarcaRevDocElement extends RevDocElement {

    @DBRef
    @NotNull
    private MarcaDocElement marca;
}