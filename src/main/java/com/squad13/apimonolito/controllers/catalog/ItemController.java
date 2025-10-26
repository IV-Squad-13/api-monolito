package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.ItemDTO;
import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditItemDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemDTO;
import com.squad13.apimonolito.services.catalog.ItemAmbienteService;
import com.squad13.apimonolito.services.catalog.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<ResItemDTO>> getAll(@ModelAttribute LoadCatalogParamsDTO loadDTO) {
        return ResponseEntity.ok(itemService.findAll(loadDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResItemDTO> getById(@PathVariable Long id, @ModelAttribute("loadAll") LoadCatalogParamsDTO loadDTO) {
        return ResponseEntity.ok(itemService.findById(id, loadDTO));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResItemDTO>> getByAttribute(
            @RequestParam String attribute,
            @RequestParam(required = false) String value,
            @ModelAttribute LoadCatalogParamsDTO loadDTO
    ) {
        return ResponseEntity.ok(itemService.findByAttribute(attribute, value, loadDTO));
    }

    @GetMapping("/rel")
    public ResponseEntity<List<ResAmbienteDTO>> getAssociations(@RequestParam(required = false) Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemAmbienteService.findItemAmbientes(id));
    }

    @PostMapping("/new")
    public ResponseEntity<ResItemDTO> create(@RequestBody @Valid ItemDTO dto) {
        return ResponseEntity.ok(itemService.createItem(dto));
    }

    @PostMapping("/{id}/ambiente")
    public ResponseEntity<ResItemAmbienteDTO> addAssociation(@PathVariable Long id, @RequestParam Long ambienteId) {
        return ResponseEntity.ok(itemAmbienteService.associateItemAndAmbiente(id, ambienteId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResItemDTO> edit(@PathVariable Long id, @RequestBody @Valid EditItemDTO dto) {
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
    public ResponseEntity<ResItemDTO> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.deactivateItem(id));
    }
}