package com.squad13.apimonolito.DTO.catalog.res;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResMinItemDTO extends ResMinDTO {
    private String desc;
}