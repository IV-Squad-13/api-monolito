package com.squad13.apimonolito.controllers.catalog;


import com.squad13.apimonolito.DTO.catalog.AmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditAmbienteDTO;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.services.catalog.AmbienteService;
import com.squad13.apimonolito.services.catalog.ItemAmbienteService;
import jakarta.validation.Valid;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/ambiente")
public class AmbienteController {

    @Autowired
    private AmbienteService ambienteService;

    @Autowired
    private ItemAmbienteService itemAmbienteService;

    @GetMapping
    public List<Ambiente> getAll() {
        return ambienteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ambiente> getById(@PathVariable Long id) {
        return ambienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Ambiente>> getByAttribute(@RequestParam String attribute, @RequestParam String value) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ambienteService.findByAttribute(attribute, value));
    }

    @GetMapping("/rel")
    public ResponseEntity<List<ItemDesc>> getAssociations(@RequestParam(required = false) Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemAmbienteService.findAmbienteItems(id));
    }

    @PostMapping("/new")
    public ResponseEntity<AmbienteDTO> create(@RequestBody @Valid AmbienteDTO dto) {
        return ResponseEntity.ok(ambienteService.createAmbiente(dto));
    }

    @PostMapping("/{id}/item")
    public ResponseEntity<ItemAmbiente> addAssociation(@PathVariable Long id, @RequestParam Long itemId) {
        return ResponseEntity.ok(itemAmbienteService.associateItemAndAmbiente(itemId, id));
    }

    @PutMapping
    public ResponseEntity<AmbienteDTO> edit(@RequestBody @Valid EditAmbienteDTO dto) {
        AmbienteDTO updated = ambienteService.updateAmbiente(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
            ambienteService.deleteAmbiente(id);
            return ResponseEntity.ok("Ambiente excluído com sucesso.");
    }

    @DeleteMapping("/{id}/item")
    public ResponseEntity<?> deleteAssociation(@PathVariable Long id, @RequestParam Long itemId) {
        itemAmbienteService.deleteItemAndAmbienteAssociation(itemId, id);
        return ResponseEntity.ok("Item removido do ambiente com sucesso.");
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        EditAmbienteDTO dto = new EditAmbienteDTO();
        dto.setId(id);
        dto.setIsActive(false);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ambienteService.updateAmbiente(dto));
    }
}