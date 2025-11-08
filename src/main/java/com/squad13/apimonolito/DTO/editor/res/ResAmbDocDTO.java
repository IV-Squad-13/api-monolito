package com.squad13.apimonolito.DTO.editor.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> itemIds = new ArrayList<>();

    private List<ResItemDocDTO> items = new ArrayList<>();

    public static ResAmbDocDTO fromDoc(AmbienteDocElement doc) {
        ResAmbDocDTO ambiente = ResDocElementDTO.fromDoc(doc, ResAmbDocDTO::new);
        ambiente.setLocal(doc.getLocal());
        ambiente.setItemIds(doc.getItemIds());

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