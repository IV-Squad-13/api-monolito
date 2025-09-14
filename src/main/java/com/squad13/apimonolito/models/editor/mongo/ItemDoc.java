package com.squad13.apimonolito.models.editor.mongo;

import com.squad13.apimonolito.models.editor.structures.ElementDoc;
import com.squad13.apimonolito.util.annotations.MongoEntityType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@MongoEntityType("ITEM")
@Document(collection = "items")
public class ItemDoc extends ElementDoc {

    @NotBlank
    private String desc;

    @Transient
    private List<ElementDoc> elementDocList;
}
