package com.squad13.apimonolito.services.catalog;

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

    private ItemDTO mapToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setName(item.getName());
        dto.setIsActive(item.getIsActive());
        dto.setDesc(item.getDesc());
        return dto;
    }
}
