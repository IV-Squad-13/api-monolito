package com.squad13.apimonolito.util.mappers;

import com.squad13.apimonolito.dto.res.SpecAssociationResDTO;
import com.squad13.apimonolito.dto.res.SpecResDTO;
import com.squad13.apimonolito.models.catalog.*;
import com.squad13.apimonolito.util.enums.CatalogSpecEnum;
import org.springframework.stereotype.Component;

@Component
public class CatalogMapper {

    private CatalogSpecEnum getSpecEnum(CatalogEntity entity) {
        if (entity instanceof Material) return CatalogSpecEnum.MATERIAL;
        if (entity instanceof Marca) return CatalogSpecEnum.MARCA;
        if (entity instanceof Ambiente) return CatalogSpecEnum.AMBIENTE;
        if (entity instanceof Item) return CatalogSpecEnum.ITEM;
        throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
    }

    public SpecResDTO toDto(CatalogEntity entity) {
        return new SpecResDTO(
                entity.getId(),
                entity.getName(),
                entity.getIsActive(),
                getSpecEnum(entity),
                entity.getAssociations()
        );
    };
}