package com.squad13.apimonolito.DTO.editor.res;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResAmbDocDTO extends ResDocElementDTO {

    private LocalEnum local;
    private List<String> itemIds = new ArrayList<>();
    private List<ResItemDocDTO> items = new ArrayList<>();

    public static ResAmbDocDTO fromDoc(AmbienteDocElement doc) {
        ResAmbDocDTO ambiente = ResDocElementDTO.fromDoc(doc, ResAmbDocDTO.class);
        ambiente.setLocal(doc.getLocal());
        ambiente.setItemIds(doc.getItemIds().stream()
                .map(ObjectId::toHexString)
                .toList());

        if (doc.getItems() != null && !doc.getItems().isEmpty()) {
            ambiente.setItems(
                    doc.getItems().stream()
                            .map(ResItemDocDTO::fromDoc)
                            .toList()
            );
        }

        return ambiente;
    }
}