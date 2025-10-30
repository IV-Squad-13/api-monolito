package com.squad13.apimonolito.DTO.catalog.res;

import lombok.Data;

@Data
public class ResComposicaoDTO {

    private Long id;

    private ResPadraoDTO padrao;

    private ResItemAmbienteDTO compAmbiente = null;

    private ResMarcaMaterialDTO compMaterial = null;
}