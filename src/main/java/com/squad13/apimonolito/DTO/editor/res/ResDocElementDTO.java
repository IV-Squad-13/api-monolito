package com.squad13.apimonolito.DTO.editor.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResDocElementDTO {
    private String id;
    private Long catalogId;
    private String name;
    private boolean inSync;
}