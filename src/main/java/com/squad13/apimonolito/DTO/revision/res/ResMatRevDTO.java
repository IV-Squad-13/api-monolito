package com.squad13.apimonolito.DTO.revision.res;

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
    private List<String> marcaRevIds = new ArrayList<>();
    private List<ResMarRevDTO> marcaRevs =  new ArrayList<>();

    public static ResMatRevDTO fromDoc(MaterialRevDocElement doc) {
        ResMatRevDTO dto = ResRevDocDTO.fromDoc(doc, ResMatRevDTO::new);
        dto.setRevisedDoc(ResMatDocDTO.fromDoc(doc.getDoc()));

        dto.setMarcaRevIds(
                Optional.ofNullable(doc.getMarcaRevIds())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ObjectId::toHexString)
                        .toList()
        );

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