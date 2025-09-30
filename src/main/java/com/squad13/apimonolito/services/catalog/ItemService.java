package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.EditItemDTO;
import com.squad13.apimonolito.DTO.catalog.ItemDTO;
import com.squad13.apimonolito.models.catalog.Item;
import com.squad13.apimonolito.repository.catalog.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;


    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public ItemDTO createItem(ItemDTO dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setIsActive(dto.getIsActive());
        item.setDesc(dto.getDesc());

        Item saved = itemRepository.save(item);
        return mapToDTO(saved);
    }

    public ItemDTO editItem(EditItemDTO dto) {
        Item item = itemRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }
        if (dto.getDesc() != null && !dto.getDesc().isBlank()) {
            item.setDesc(dto.getDesc());
        }
        if (dto.getIsActive() != null) {
            item.setIsActive(dto.getIsActive());
        }

        Item updated = itemRepository.save(item);
        return mapToDTO(updated);
    }

    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));
        itemRepository.delete(item);
    }

    private ItemDTO mapToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setName(item.getName());
        dto.setIsActive(item.getIsActive());
        dto.setDesc(item.getDesc());
        return dto;
    }
}
