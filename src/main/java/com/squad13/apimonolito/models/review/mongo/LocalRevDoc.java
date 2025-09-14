package com.squad13.apimonolito.models.review.mongo;

import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.models.review.structures.ElementRevDoc;
import com.squad13.apimonolito.util.annotations.MongoEntityType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@MongoEntityType("LOCAL")
@Document(collection = "local_rev")
public class LocalRevDoc extends ElementRevDoc {

    @DBRef
    @NotNull
    private LocalDoc local;

    @DBRef
    private List<AmbienteRevDoc> ambienteRevDocList;
}
