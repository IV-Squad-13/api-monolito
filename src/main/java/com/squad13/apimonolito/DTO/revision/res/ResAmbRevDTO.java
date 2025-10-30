package com.squad13.apimonolito.DTO.revision.res;

import com.squad13.apimonolito.DTO.editor.res.ResAmbDocDTO;
import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.revision.mongo.AmbienteRevDocElement;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDocElement;
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
public class ResAmbRevDTO extends ResRevDocDTO {

    private ResAmbDocDTO revisedDoc;
    private List<String> itemRevIds =  new ArrayList<>();
    private List<ResItemRevDTO> itemRevs =  new ArrayList<>();

    public static ResAmbRevDTO fromDoc(AmbienteRevDocElement doc) {
        ResAmbRevDTO dto = ResRevDocDTO.fromDoc(doc, ResAmbRevDTO::new);
        if (doc.getDoc() != null) {
            dto.setRevisedDoc(ResAmbDocDTO.fromDoc(doc.getDoc()));
        }

        dto.setItemRevIds(
                Optional.ofNullable(doc.getItemRevIds())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ObjectId::toHexString)
                        .toList()
        );

        dto.setItemRevs(
                Optional.ofNullable(doc.getItemRevs())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ResItemRevDTO::fromDoc)
                        .toList()
        );

        return dto;
    }
}