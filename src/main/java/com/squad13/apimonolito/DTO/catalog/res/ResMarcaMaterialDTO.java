package com.squad13.apimonolito.DTO.catalog.res;

public record ResMarcaMaterialDTO(
        Long id,
        ResMarcaDTO marca,
        ResMaterialDTO material
) {
}