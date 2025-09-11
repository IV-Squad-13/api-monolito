package com.squad13.apimonolito.services;

import com.squad13.apimonolito.dto.req.SpecReqDTO;
import com.squad13.apimonolito.dto.res.SpecResDTO;
import com.squad13.apimonolito.models.catalog.CatalogEntity;
import com.squad13.apimonolito.repository.*;
import com.squad13.apimonolito.util.enums.CatalogSpecEnum;
import com.squad13.apimonolito.util.mappers.CatalogMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CatalogService {

    private final Map<CatalogSpecEnum, CatalogRepository<? extends CatalogEntity, Long>> catalogMap;
    private final CatalogMapper catalogMapper;

    public CatalogService(AmbienteRepository ambienteRepository, ItemRepository itemRepository, MarcaRepository marcaRepository,
                          MaterialRepository materialRepository, CatalogMapper catalogMapper) {
        this.catalogMapper = catalogMapper;

        this.catalogMap = Map.of(
                CatalogSpecEnum.AMBIENTE, ambienteRepository,
                CatalogSpecEnum.ITEM, itemRepository,
                CatalogSpecEnum.MATERIAL, materialRepository,
                CatalogSpecEnum.MARCA, marcaRepository
        );
    }

    public List<SpecResDTO> findAllBySpec(CatalogSpecEnum specType) {
        return catalogMap.get(specType).findAll()
                .stream().map(catalogMapper::toDto)
                .toList();
    }

    public SpecResDTO findSingleSpec(CatalogSpecEnum specType, Optional<Long> specId, Optional<String> specName) {
        if (specId.isEmpty() && specName.isPresent()) throw new IllegalArgumentException("Nome ou ID da especificação não fornecido");

        var repository = catalogMap.get(specType);
        return specId.flatMap(repository::findById)
                .map(catalogMapper::toDto)
                .or(() -> specName.flatMap(repository::findByName).map(catalogMapper::toDto))
                .orElseThrow(() -> new IllegalArgumentException("Especificação não encontrada"));
    }

    public URI createSpec(CatalogSpecEnum specType, SpecReqDTO specReqDTO) {
        var repository = catalogMap.get(specType);

        if (repository.findByName(specReqDTO.name()).isPresent())
            throw new IllegalArgumentException("Especificação do tipo " + specReqDTO.specType() + " com o nome " + specReqDTO.name() + " já existe");

        CatalogEntity catalogEntity = catalogMapper.toEntity(specReqDTO);

        @SuppressWarnings("unchecked")
        CatalogRepository<CatalogEntity, Long> typedRepo = (CatalogRepository<CatalogEntity, Long>) repository;
        typedRepo.save(catalogEntity);

        return URI.create(catalogEntity.getId().toString());
    }
}