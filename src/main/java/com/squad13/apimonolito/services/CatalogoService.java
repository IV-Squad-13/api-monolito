package com.squad13.apimonolito.services;

import com.squad13.apimonolito.dto.res.SpecResDTO;
import com.squad13.apimonolito.models.catalog.CatalogEntity;
import com.squad13.apimonolito.repository.AmbienteRepository;
import com.squad13.apimonolito.repository.ItemRepository;
import com.squad13.apimonolito.repository.MarcaRepository;
import com.squad13.apimonolito.repository.MaterialRepository;
import com.squad13.apimonolito.util.enums.CatalogSpecEnum;
import com.squad13.apimonolito.util.mappers.CatalogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CatalogoService {

    private final AmbienteRepository ambienteRepository;
    private final MarcaRepository marcaRepository;
    private final MaterialRepository materialRepository;
    private final ItemRepository itemRepository;

    private final Map<CatalogSpecEnum, JpaRepository<? extends CatalogEntity, Long>> catalogMap;

    private final CatalogMapper catalogMapper;

    public CatalogoService(AmbienteRepository ambienteRepository, ItemRepository itemRepository,
                           MarcaRepository marcaRepository, MaterialRepository materialRepository, CatalogMapper catalogMapper) {
        this.ambienteRepository = ambienteRepository;
        this.marcaRepository = marcaRepository;
        this.materialRepository = materialRepository;
        this.itemRepository = itemRepository;
        this.catalogMapper = catalogMapper;

        this.catalogMap = Map.of(
                CatalogSpecEnum.AMBIENTE, ambienteRepository,
                CatalogSpecEnum.ITEM, itemRepository,
                CatalogSpecEnum.MATERIAL, materialRepository,
                CatalogSpecEnum.MARCA, marcaRepository
        );
    }

    public List<SpecResDTO> findAllBySpec(CatalogSpecEnum spec) {
        return catalogMap.get(spec).findAll()
                .stream().map(catalogMapper::toDto)
                .collect(Collectors.toList());
    }
}