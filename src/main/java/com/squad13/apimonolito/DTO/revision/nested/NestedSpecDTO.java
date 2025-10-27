package com.squad13.apimonolito.DTO.revision.nested;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NestedSpecDTO {

    private ObjectId id;

    private List<NestedLocalDTO> locais;
    private List<NestedMaterialDTO> materiais;
}