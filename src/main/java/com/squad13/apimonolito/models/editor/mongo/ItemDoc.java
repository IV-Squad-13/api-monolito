package com.squad13.apimonolito.models.editor.mongo;

import com.squad13.apimonolito.models.editor.structures.ElementDoc;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "items")
@CompoundIndex(name = "catalog_name_unique", def = "{'catalogId' : 1, 'name': 1, 'empreendimento': 1}", unique = true)
public class ItemDoc extends ElementDoc {

    @NotBlank
    private String desc;

    @Transient
    private List<ElementDoc> elementDocList;
}
