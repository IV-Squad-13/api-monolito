package com.squad13.apimonolito.DTO.catalog.res;

import java.util.List;

public record ResPadraoDTO(
        Long id,
        String name,
        Boolean isActive,

        List<ResMinDTO> ambienteList,
        List<ResMinDTO> itemList,
        List<ResMinDTO> materialList,
        List<ResMinDTO> marcaList
) {
}