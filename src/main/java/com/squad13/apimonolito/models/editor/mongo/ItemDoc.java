package com.squad13.apimonolito.models.editor.mongo;

import com.squad13.apimonolito.models.editor.structures.DocElement;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "items")
public class ItemDoc {

    @NotBlank
    private String desc;

    @Transient
    private List<DocElement> docElementList;
}
