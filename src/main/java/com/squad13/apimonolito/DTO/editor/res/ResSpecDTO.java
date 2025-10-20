package com.squad13.apimonolito.DTO.editor.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResSpecDTO {
    private String id;
    private Long empreendimentoId;
    private String name;
    private String desc;
    private String obs;

    private List<ResLocalDocDTO> locais = new ArrayList<>();
    private List<ResMatDocDTO> materiais = new ArrayList<>();
}