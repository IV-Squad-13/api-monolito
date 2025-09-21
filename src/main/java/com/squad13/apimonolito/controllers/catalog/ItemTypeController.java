package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.edit.EditItemTypeDTO;
import com.squad13.apimonolito.DTO.catalog.ItemTypeDTO;
import com.squad13.apimonolito.models.catalog.ItemType;
import com.squad13.apimonolito.services.catalog.ItemTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/item-types")
public class ItemTypeController {

    @Autowired
    private ItemTypeService itemTypeService;

    @GetMapping
    public ResponseEntity<List<ItemType>> getAll() {
        return ResponseEntity.ok(itemTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemType> getById(@PathVariable Long id) {
        return ResponseEntity.ok(itemTypeService.findByIdOrThrow(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ItemType> getByName(@PathVariable String name) {
        return ResponseEntity.ok(itemTypeService.findByNameOrThrow(name));
    }

    @PostMapping("/new")
    public ResponseEntity<ItemType> create(@RequestBody @Valid ItemTypeDTO dto) {
        return ResponseEntity.ok(itemTypeService.createItemType(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemType> edit(@PathVariable Long id, @RequestBody EditItemTypeDTO dto) {
        return ResponseEntity.ok(itemTypeService.updateItemType(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        itemTypeService.deleteItemType(id);
        return ResponseEntity.ok("ItemType excluído com sucesso.");
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ItemType> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(itemTypeService.deactivateItemType(id));
    }
}