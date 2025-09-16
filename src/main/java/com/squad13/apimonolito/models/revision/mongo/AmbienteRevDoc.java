package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDoc;
import com.squad13.apimonolito.models.revision.structures.ElementRevDoc;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "ambiente_rev")
@CompoundIndex(name = "catalog_name_unique", def = "{'revisaoId' : 1, 'ambiente': 1}", unique = true)
public class AmbienteRevDoc extends ElementRevDoc {

    @DBRef
    @NotNull
    private AmbienteDoc ambiente;

    @DBRef
    private List<ItemRevDoc> itemRevList;
}