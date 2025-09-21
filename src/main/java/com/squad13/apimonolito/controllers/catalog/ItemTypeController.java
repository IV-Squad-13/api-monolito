package com.squad13.apimonolito.controllers.catalog;

import com.oracle.svm.core.annotate.Inject;
import com.squad13.apimonolito.DTO.catalog.EditItemTypeDTO;
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
        return ResponseEntity.ok(itemTypeService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemType> update(@PathVariable Long id, @RequestBody EditItemTypeDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(itemTypeService.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        itemTypeService.delete(id);
        return ResponseEntity.ok("ItemType excluído com sucesso.");
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ItemType> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(itemTypeService.deactivate(id));
    }
}