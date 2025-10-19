package com.squad13.apimonolito.DTO.catalog.res;

import java.util.List;
import java.util.Map;

public record ResPadraoDTO(
        Long id,
        String name,
        Boolean isActive,

        Map<ResAmbienteDTO, List<ResItemDTO>> ambienteSet,
        Map<ResMaterialDTO, List<ResMarcaDTO>> materialSet
) { }