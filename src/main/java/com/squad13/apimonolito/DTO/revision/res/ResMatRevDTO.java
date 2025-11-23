package com.squad13.apimonolito.DTO.revision.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.DTO.editor.res.ResMatDocDTO;
import com.squad13.apimonolito.models.revision.mongo.MaterialRevDocElement;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResMatRevDTO extends ResRevDocDTO {

    private ResMatDocDTO revisedDoc;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> marcaRevIds = new ArrayList<>();

    private List<ResMarRevDTO> marcaRevs =  new ArrayList<>();

    public static ResMatRevDTO fromDoc(MaterialRevDocElement doc) {
        ResMatRevDTO dto = ResRevDocDTO.fromDoc(doc, ResMatRevDTO::new);
        if (doc.getDoc() != null) {
            dto.setRevisedDoc(ResMatDocDTO.fromDoc(doc.getDoc()));
        }

        dto.setMarcaRevIds(doc.getMarcaRevIds());

        dto.setMarcaRevs(
                Optional.ofNullable(doc.getMarcaRevs())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ResMarRevDTO::fromDoc)
                        .toList()
        );

        return dto;
    }
}