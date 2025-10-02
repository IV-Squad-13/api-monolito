package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.edit.EditItemTypeDTO;
import com.squad13.apimonolito.DTO.catalog.ItemTypeDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemTypeDTO;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.ItemType;
import com.squad13.apimonolito.repository.catalog.ItemTypeRepository;
import com.squad13.apimonolito.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemTypeService {

    private final ItemTypeRepository itemTypeRepository;

    private final Mapper mapper;

    public List<ResItemTypeDTO> findAll() {
        return itemTypeRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    private ItemType findByIdOrThrow(Long id) {
        return itemTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de item com id " + id + " não encontrado."));
    }

    public ResItemTypeDTO findById(Long id) {
        return itemTypeRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Item com ID: " + id + " não encontrado."));
    }

    public ResItemTypeDTO findByNameOrThrow(String name) {
        return itemTypeRepository.findByName(name)
                .map(mapper::toResponse)
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

        return mapper.toResponse(itemTypeRepository.save(itemType));
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

        return mapper.toResponse(itemTypeRepository.save(existing));
    }

    public void deleteItemType(Long id) {
        ItemType existing = findByIdOrThrow(id);
        itemTypeRepository.delete(existing);
    }

    public ResItemTypeDTO deactivateItemType(Long id) {
        ItemType existing = findByIdOrThrow(id);
        existing.setIsActive(false);
        return mapper.toResponse(itemTypeRepository.save(existing));
    }
}
