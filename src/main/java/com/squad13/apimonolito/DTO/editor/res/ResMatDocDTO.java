package com.squad13.apimonolito.DTO.editor.res;

import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResMatDocDTO extends ResDocElementDTO {
    private List<String> marcas = new ArrayList<>();

    public static ResMatDocDTO fromDoc(MaterialDocElement doc) {
        ResMatDocDTO mat = ResDocElementDTO.fromDoc(doc, ResMatDocDTO.class);
        mat.setMarcas(doc.getMarcaIds().stream()
                .map(ObjectId::toHexString)
                .toList());
        return mat;
    }
}