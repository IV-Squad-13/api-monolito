package com.squad13.apimonolito.DTO.editor.res;


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
}