package com.squad13.apimonolito.DTO.editor.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> marcaIds = new ArrayList<>();

    private List<ResMarDocDTO> marcas = new ArrayList<>();

    public static ResMatDocDTO fromDoc(MaterialDocElement doc) {
        ResMatDocDTO mat = ResDocElementDTO.fromDoc(doc, ResMatDocDTO::new);
        mat.setMarcaIds(doc.getMarcaIds());

        if (doc.getMarcas() != null && !doc.getMarcas().isEmpty()) {
            mat.setMarcas(
                    doc.getMarcas().stream()
                            .map(ResMarDocDTO::fromDoc)
                            .toList()
            );
        }

        return mat;
    }
}