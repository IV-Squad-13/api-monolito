package com.squad13.apimonolito.DTO.editor.res;

import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResLocalDocDTO {
        private String id;
        private LocalEnum local;
        private List<ResAmbDocDTO> ambientes;
}