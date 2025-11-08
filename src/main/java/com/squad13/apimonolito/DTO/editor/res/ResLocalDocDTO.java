package com.squad13.apimonolito.DTO.editor.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private LocalEnum local;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> ambienteIds = new ArrayList<>();
    private List<ResAmbDocDTO> ambientes = new ArrayList<>();

    public static ResLocalDocDTO fromDoc(LocalDoc doc) {
        ResLocalDocDTO dto = new ResLocalDocDTO();
        dto.setId(doc.getId());
        dto.setLocal(doc.getLocal());

        dto.setAmbienteIds(doc.getAmbienteIds());

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