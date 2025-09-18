package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.EditItemDTO;
import com.squad13.apimonolito.DTO.catalog.ItemDTO;
import com.squad13.apimonolito.models.catalog.Item;
import com.squad13.apimonolito.services.catalog.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<Item> getAll() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/new")
    public ResponseEntity<ItemDTO> create(@RequestBody ItemDTO dto) {
        return ResponseEntity.ok(itemService.createItem(dto));
    }

    @PutMapping
    public ResponseEntity<ItemDTO> edit(@RequestBody EditItemDTO dto) {
        ItemDTO updated = itemService.editItem(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
            itemService.deleteItem(id);
            return ResponseEntity.ok("Item excluído com sucesso.");
    }
}