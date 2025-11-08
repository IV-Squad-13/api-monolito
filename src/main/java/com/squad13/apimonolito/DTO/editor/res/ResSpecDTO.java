package com.squad13.apimonolito.DTO.editor.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.DTO.revision.res.ResLocalRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResMatRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDocDTO;
import com.squad13.apimonolito.DTO.revision.res.ResSpecRevDTO;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.revision.mongo.EspecificacaoRevDocElement;
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
@NoArgsConstructor
@AllArgsConstructor
public class ResSpecDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private Long empreendimentoId;
    private String name;
    private String desc;
    private String obs;

    private List<ResLocalDocDTO> locais = new ArrayList<>();
    private List<ResMatDocDTO> materiais = new ArrayList<>();
}