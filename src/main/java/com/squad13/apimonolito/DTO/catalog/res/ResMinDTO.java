package com.squad13.apimonolito.DTO.catalog.res;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ResMinDTO {
        private Long id;
        private String name;
        private Boolean isActive;
        private List<Long> associatedIds;
}