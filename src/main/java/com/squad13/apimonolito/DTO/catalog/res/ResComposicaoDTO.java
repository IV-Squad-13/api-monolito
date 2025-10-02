package com.squad13.apimonolito.DTO.catalog.res;

import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.models.catalog.associative.ComposicaoAmbiente;
import com.squad13.apimonolito.models.catalog.associative.ComposicaoMaterial;
import lombok.Data;

@Data
public class ResComposicaoDTO {

    private Long id;

    private ResPadraoDTO padrao;

    private ResItemAmbienteDTO compAmbiente = null;

    private ResMarcaMaterialDTO compMaterial = null;
}