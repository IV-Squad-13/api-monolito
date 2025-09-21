package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.models.catalog.Catalogo;
import com.squad13.apimonolito.repository.catalog.CatalogoRepository;
import com.squad13.apimonolito.repository.catalog.ComposicaoAmbienteRepository;
import com.squad13.apimonolito.repository.catalog.ComposicaoMaterialRepository;
import com.squad13.apimonolito.util.enums.CompositorEnum;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ComposicaoService {

    private final CatalogoRepository catalogoRepository;
    private final ComposicaoAmbienteRepository compAmbienteRepository;
    private final ComposicaoMaterialRepository compMaterialRepository;
}