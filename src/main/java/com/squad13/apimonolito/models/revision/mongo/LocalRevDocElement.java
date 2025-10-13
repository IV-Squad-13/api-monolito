package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.LocalDocElement;
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
@Document(collection = "local_rev")
@CompoundIndex(name = "local_rev_unique", def = "{'revisaoId' : 1, 'local': 1}", unique = true)
public class LocalRevDocElement extends RevDocElement {

    @DBRef
    @NotNull
    private LocalDocElement local;

    @DBRef
    private List<AmbienteRevDocElement> ambienteRevList;
}
