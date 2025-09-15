package com.squad13.apimonolito.services;

import com.squad13.apimonolito.DTO.ItemDTO;
import com.squad13.apimonolito.models.Item;
import com.squad13.apimonolito.models.TipoItem;
import com.squad13.apimonolito.repository.ItemRepository;
import com.squad13.apimonolito.repository.TipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TipoRepository tipoRepository;


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
        item.setWidth(dto.getWidth());
        item.setHeight(dto.getHeight());
        item.setDesc(dto.getDesc());


        if (dto.getTypeId() != null) {
            TipoItem tipo = tipoRepository.findById(dto.getTypeId())
                    .orElseThrow(() -> new RuntimeException("TipoItem não encontrado com ID: " + dto.getTypeId()));
            item.setType(tipo);
        }

        Item saved = itemRepository.save(item);
        return mapToDTO(saved);
    }

    private ItemDTO mapToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setName(item.getName());
        dto.setIsActive(item.getIsActive());
        dto.setWidth(item.getWidth());
        dto.setHeight(item.getHeight());
        dto.setDesc(item.getDesc());
        dto.setTypeId(item.getType() != null ? item.getType().getId() : null);
        return dto;
    }
}
