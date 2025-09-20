package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.EditItemDTO;
import com.squad13.apimonolito.DTO.catalog.ItemDTO;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.services.catalog.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<ItemDesc> getAll() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDesc> getById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDesc>> getByAttribute(@RequestParam String attribute, @RequestParam(required = false) String value) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.findByAttribute(attribute, value));
    }

    @PostMapping("/new")
    public ResponseEntity<ItemDTO> create(@RequestBody @Valid ItemDTO dto) {
        return ResponseEntity.ok(itemService.createItem(dto));
    }

    @PutMapping
    public ResponseEntity<ItemDTO> edit(@RequestBody @Valid EditItemDTO dto) {
        ItemDTO updated = itemService.editItem(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
            itemService.deleteItem(id);
            return ResponseEntity.ok("Item excluído com sucesso.");
    }
}