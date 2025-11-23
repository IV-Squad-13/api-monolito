package com.squad13.apimonolito.DTO.revision.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.DTO.editor.res.*;
import com.squad13.apimonolito.models.revision.mongo.LocalRevDocElement;
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
public class ResLocalRevDTO extends ResRevDocDTO {

    private ResLocalDocDTO revisedDoc;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> ambienteRevIds = new ArrayList<>();

    private List<ResAmbRevDTO> ambienteRevs = new ArrayList<>();

    public static ResLocalRevDTO fromDoc(LocalRevDocElement doc) {
        ResLocalRevDTO dto = ResRevDocDTO.fromDoc(doc, ResLocalRevDTO::new);
        if (doc.getDoc() != null) {
            dto.setRevisedDoc(ResLocalDocDTO.fromDoc(doc.getDoc()));
        }

        dto.setAmbienteRevIds(doc.getAmbienteRevIds());

        dto.setAmbienteRevs(
                Optional.ofNullable(doc.getAmbienteRevs())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ResAmbRevDTO::fromDoc)
                        .toList()
        );

        return dto;
    }
}