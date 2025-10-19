package com.squad13.apimonolito.DTO.editor.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ResDocElementDTO {
    private String id;
    private Long catalogId;
    private String name;
    private boolean inSync;

    protected ResDocElementDTO(ResDocElementDTO source) {
        this.id = source.getId();
        this.catalogId = source.getCatalogId();
        this.name = source.getName();
        this.inSync = source.isInSync();
    }
}