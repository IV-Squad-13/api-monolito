package com.squad13.apimonolito.DTO.revision.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.DTO.editor.res.ResSpecDTO;
import com.squad13.apimonolito.models.revision.mongo.EspecificacaoRevDocElement;

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
public class ResSpecRevDTO extends ResRevDocDTO {

    private ResSpecDTO revisedDoc;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> localRevIds;
    private List<ResLocalRevDTO> localRevs = new ArrayList<>();

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> materialRevIds;
    private List<ResMatRevDTO> materialRevs = new ArrayList<>();

    private Boolean isNameApproved;
    private Boolean isDescApproved;
    private Boolean isObsApproved;

    public static ResSpecRevDTO fromDoc(EspecificacaoRevDocElement doc, ResSpecDTO resSpec) {
        ResSpecRevDTO dto = ResRevDocDTO.fromDoc(doc, ResSpecRevDTO::new);

        if (resSpec != null) {
            dto.setRevisedDoc(resSpec);
        }

        dto.setLocalRevIds(doc.getLocalRevIds());

        dto.setLocalRevs(
                Optional.ofNullable(doc.getLocalRevs())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ResLocalRevDTO::fromDoc)
                        .toList()
        );

        dto.setMaterialRevIds(doc.getMaterialRevIds());

        dto.setMaterialRevs(
                Optional.ofNullable(doc.getMaterialRevs())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ResMatRevDTO::fromDoc)
                        .toList()
        );

        return dto;
    }
}