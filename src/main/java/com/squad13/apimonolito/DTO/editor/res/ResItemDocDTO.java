package com.squad13.apimonolito.DTO.editor.res;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResItemDocDTO extends ResDocElementDTO {
    private String desc;
    private String type;
    private Long typeId;

    public ResItemDocDTO(String id, Long catalogId, String name, boolean inSync, String desc, String type, Long typeId) {
        super(id, catalogId, name, inSync);
        this.desc = desc;
        this.type = type;
        this.typeId = typeId;
    }
}