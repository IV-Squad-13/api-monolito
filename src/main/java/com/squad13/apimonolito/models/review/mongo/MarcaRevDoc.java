package com.squad13.apimonolito.models.review.mongo;

import com.squad13.apimonolito.models.editor.mongo.MarcaDoc;
import com.squad13.apimonolito.models.review.structures.ElementRevDoc;
import com.squad13.apimonolito.util.annotations.MongoEntityType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = false)
@MongoEntityType("MARCA")
@Document(collection = "marcas_rev")
public class MarcaRevDoc extends ElementRevDoc {

    @DBRef
    @NotNull
    private MarcaDoc marcaDoc;
}