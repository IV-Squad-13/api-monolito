package com.squad13.apimonolito.DTO.revision.res;

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

    private List<String> localRevIds;
    private List<ResLocalRevDTO> localRevs = new ArrayList<>();

    private List<String> materialRevIds;
    private List<ResMatRevDTO> materialRevs = new ArrayList<>();

    private Boolean isNameApproved;
    private Boolean isDescApproved;
    private Boolean isObsApproved;

    public static ResSpecRevDTO fromDoc(EspecificacaoRevDocElement doc, ResSpecDTO resSpec) {
        ResSpecRevDTO dto = ResRevDocDTO.fromDoc(doc, ResSpecRevDTO::new);
        dto.setRevisedDoc(resSpec);

        dto.setLocalRevIds(
                Optional.ofNullable(doc.getLocalRevIds())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ObjectId::toHexString)
                        .toList()
        );

        dto.setLocalRevs(
                Optional.ofNullable(doc.getLocalRevs())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ResLocalRevDTO::fromDoc)
                        .toList()
        );

        dto.setMaterialRevIds(
                Optional.ofNullable(doc.getMaterialRevIds())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ObjectId::toHexString)
                        .toList()
        );

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