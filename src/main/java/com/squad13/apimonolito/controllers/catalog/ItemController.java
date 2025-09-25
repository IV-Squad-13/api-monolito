package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.edit.EditItemDTO;
import com.squad13.apimonolito.DTO.catalog.ItemDTO;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.services.catalog.ItemAmbienteService;
import com.squad13.apimonolito.services.catalog.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final ItemAmbienteService itemAmbienteService;

    @GetMapping
    public List<ItemDTO> getAll() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDesc> getById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.findByIdOrThrow(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDesc>> getByAttribute(@RequestParam String attribute, @RequestParam(required = false) String value) {
        return ResponseEntity.ok(itemService.findByAttribute(attribute, value));
    }

    @GetMapping("/rel")
    public ResponseEntity<List<Ambiente>> getAssociations(@RequestParam(required = false) Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemAmbienteService.findItemAmbientes(id));
    }

    @PostMapping("/new")
    public ResponseEntity<ItemDTO> create(@RequestBody @Valid ItemDTO dto) {
        return ResponseEntity.ok(itemService.createItem(dto));
    }

    @PostMapping("/{id}/ambiente")
    public ResponseEntity<ItemAmbiente> addAssociation(@PathVariable Long id, @RequestParam Long ambienteId) {
        return ResponseEntity.ok(itemAmbienteService.associateItemAndAmbiente(id, ambienteId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> edit(@PathVariable Long id, @RequestBody @Valid EditItemDTO dto) {
        return ResponseEntity.ok(itemService.updateItem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
            itemService.deleteItem(id);
            return ResponseEntity.ok("Item excluído com sucesso.");
    }

    @DeleteMapping("/{id}/ambiente")
    public ResponseEntity<?> deleteAssociation(@PathVariable Long id, @RequestParam Long ambienteId) {
        itemAmbienteService.deleteItemAndAmbienteAssociation(id, ambienteId);
        return ResponseEntity.ok("Ambiente removido do item com sucesso.");
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<ItemDesc> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.deactivateItem(id));
    }
}