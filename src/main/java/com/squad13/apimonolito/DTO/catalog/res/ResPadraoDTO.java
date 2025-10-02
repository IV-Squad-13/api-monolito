package com.squad13.apimonolito.DTO.catalog.res;

import java.util.Set;

public record ResPadraoDTO(
        Long id,
        String name,
        Boolean isActive,

        Set<ResMinDTO> itemSet,
        Set<ResMinDTO> ambienteSet,
        Set<ResMinDTO> marcaSet,
        Set<ResMinDTO> materialSet
) { }