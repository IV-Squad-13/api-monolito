package com.squad13.apimonolito.DTO.catalog.res;

public record ResItemAmbienteDTO(
        Long id,
        ResItemDTO item,
        ResAmbienteDTO ambiente
) {
}