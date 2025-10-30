package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditItemTypeDTO;
import com.squad13.apimonolito.DTO.catalog.ItemTypeDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditItemTypeDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemTypeDTO;
import com.squad13.apimonolito.exceptions.InvalidAttributeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.ItemType;
import com.squad13.apimonolito.repository.catalog.ItemTypeRepository;
import com.squad13.apimonolito.util.mapper.CatalogMapper;
import com.squad13.apimonolito.util.mappers.CatalogMapper;
import com.squad13.apimonolito.util.search.CatalogSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemTypeService {

    private final ItemTypeRepository itemTypeRepository;

    private final CatalogMapper catalogMapper;

    private final CatalogSearch catalogSearch;

    public List<ResItemTypeDTO> findAll() {
        return itemTypeRepository.findAll()
                .stream()
                .map(catalogMapper::toResponse)
                .toList();
    }

    private ItemType findByIdOrThrow(Long id) {
        return itemTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de item com id " + id + " não encontrado."));
    }

    public ResItemTypeDTO findById(Long id) {
        return itemTypeRepository.findById(id)
                .map(catalogMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Item com ID: " + id + " não encontrado."));
    }

    public ResItemTypeDTO findByNameOrThrow(String name) {
        return itemTypeRepository.findByName(name)
                .map(catalogMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de item com nome " + name + " não encontrado."));
    }

    public ResItemTypeDTO createItemType(ItemTypeDTO dto) {
        itemTypeRepository.findByName(dto.getName())
                .ifPresent(t -> {
                    throw new ResourceAlreadyExistsException("Já existe um tipo de item com nome " + dto.getName());
                });

        ItemType itemType = new ItemType();
        itemType.setName(dto.getName());
        itemType.setIsActive(true);

        return catalogMapper.toResponse(itemTypeRepository.save(itemType));
    }

    public ResItemTypeDTO updateItemType(Long id, EditItemTypeDTO dto) {
        ItemType existing = findByIdOrThrow(id);

        if (dto.getName() != null && !dto.getName().equals(existing.getName())) {
            itemTypeRepository.findByName(dto.getName())
                    .ifPresent(t -> {
                        throw new ResourceAlreadyExistsException("Já existe um tipo de item com nome " + dto.getName());
                    });

            existing.setName(dto.getName());
        }

        if (dto.getIsActive() != null) {
            existing.setIsActive(dto.getIsActive());
        }

        return catalogMapper.toResponse(itemTypeRepository.save(existing));
    }

    public void deleteItemType(Long id) {
        ItemType existing = findByIdOrThrow(id);
        itemTypeRepository.delete(existing);
    }

    public ResItemTypeDTO deactivateItemType(Long id) {
        ItemType existing = findByIdOrThrow(id);
        existing.setIsActive(false);
        return catalogMapper.toResponse(itemTypeRepository.save(existing));
    }

    public List<ResItemTypeDTO> findByFilters(Map<String, String> stringFilters, LoadCatalogParamsDTO loadDTO) {

        Map<String, Object> typedFilters = new HashMap<>();

        for (Map.Entry<String, String> entry : stringFilters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            switch (key) {

                case "name":
                    typedFilters.put(key, value);
                    break;


                case "isActive":
                    typedFilters.put(key, Boolean.parseBoolean(value));
                    break;


                case "id":
                    try {
                        typedFilters.put(key, Long.parseLong(value));
                    } catch (NumberFormatException e) {
                        throw new InvalidAttributeException("Valor inválido para o filtro 'id': " + value);
                    }
                    break;

                default:
                    System.out.println("Ignorando filtro desconhecido: " + key);
            }
        }

        List<ItemType> itemTypes = catalogSearch.findByCriteria(typedFilters, ItemType.class);

        return itemTypes.stream()
                .map(catalogMapper::toResponse)
                .toList();
    }
}
