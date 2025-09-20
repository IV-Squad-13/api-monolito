package com.squad13.apimonolito.controllers.catalog;


import com.squad13.apimonolito.DTO.catalog.AmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.EditAmbienteDTO;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.services.catalog.AmbienteService;
import jakarta.validation.Valid;
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

    @PostMapping("/new")
    public ResponseEntity<AmbienteDTO> create(@RequestBody @Valid AmbienteDTO dto) {
        return ResponseEntity.ok(ambienteService.createAmbiente(dto));
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
}