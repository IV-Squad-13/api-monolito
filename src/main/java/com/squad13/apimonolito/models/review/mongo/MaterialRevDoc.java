package com.squad13.apimonolito.models.review.mongo;

import com.squad13.apimonolito.models.editor.mongo.MaterialDoc;
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
@MongoEntityType("MATERIAL")
@Document(collection = "material_rev")
public class MaterialRevDoc extends ElementRevDoc {

    @DBRef
    @NotNull
    private MaterialDoc material;

    @DBRef
    private List<MarcaRevDoc> marcaRevList;
}