package com.squad13.apimonolito.DTO.catalog;

import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.models.catalog.associative.ComposicaoAmbiente;
import com.squad13.apimonolito.models.catalog.associative.ComposicaoMaterial;
import lombok.Data;

@Data
public class ComposicaoDTO {

    private Long id;

    private Padrao padrao;

    private ComposicaoAmbiente compAmbiente = null;

    private ComposicaoMaterial compMaterial = null;
}