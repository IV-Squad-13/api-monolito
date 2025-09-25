package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.edit.EditItemTypeDTO;
import com.squad13.apimonolito.DTO.catalog.ItemTypeDTO;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.ItemType;
import com.squad13.apimonolito.repository.catalog.ItemTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemTypeService {

    private final ItemTypeRepository itemTypeRepository;

    public List<ItemType> findAll() {
        return itemTypeRepository.findAll();
    }

    public ItemType findByIdOrThrow(Long id) {
        return itemTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de item com id " + id + " não encontrado."));
    }

    public ItemType findByNameOrThrow(String name) {
        return itemTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de item com nome " + name + " não encontrado."));
    }

    public ItemType createItemType(ItemTypeDTO dto) {
        itemTypeRepository.findByName(dto.getName())
                .ifPresent(t -> {
                    throw new ResourceAlreadyExistsException("Já existe um tipo de item com nome " + dto.getName());
                });

        ItemType itemType = new ItemType();
        itemType.setName(dto.getName());
        itemType.setIsActive(true);

        return itemTypeRepository.save(itemType);
    }

    public ItemType updateItemType(Long id, EditItemTypeDTO dto) {
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

        return itemTypeRepository.save(existing);
    }

    public void deleteItemType(Long id) {
        ItemType existing = findByIdOrThrow(id);
        itemTypeRepository.delete(existing);
    }

    public ItemType deactivateItemType(Long id) {
        ItemType existing = findByIdOrThrow(id);
        existing.setIsActive(false);
        return itemTypeRepository.save(existing);
    }
}
