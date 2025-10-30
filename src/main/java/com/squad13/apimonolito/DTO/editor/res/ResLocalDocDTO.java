package com.squad13.apimonolito.DTO.editor.res;

import com.squad13.apimonolito.DTO.revision.res.ResMarRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResMatRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDocDTO;
import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.models.revision.mongo.MaterialRevDocElement;
import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResLocalDocDTO {
    private String id;
    private LocalEnum local;
    private List<String> ambienteIds = new ArrayList<>();
    private List<ResAmbDocDTO> ambientes = new ArrayList<>();

    public static ResLocalDocDTO fromDoc(LocalDoc doc) {
        ResLocalDocDTO dto = new ResLocalDocDTO();
        dto.setId(String.valueOf(doc.getId()));
        dto.setLocal(doc.getLocal());

        dto.setAmbienteIds(
                Optional.ofNullable(doc.getAmbienteIds())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ObjectId::toHexString)
                        .toList()
        );

        dto.setAmbientes(
                Optional.ofNullable(doc.getAmbientes())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ResAmbDocDTO::fromDoc)
                        .toList()
        );

        return dto;
    }
}