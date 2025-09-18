package com.squad13.apimonolito.controllers.catalog;


import com.squad13.apimonolito.DTO.catalog.AmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.EditAmbienteDTO;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.services.catalog.AmbienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ambientes")
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

    @PostMapping("/new")
    public ResponseEntity<AmbienteDTO> create(@RequestBody AmbienteDTO dto) {
        return ResponseEntity.ok(ambienteService.createAmbiente(dto));
    }

    @PutMapping
    public ResponseEntity<AmbienteDTO> edit(@RequestBody EditAmbienteDTO dto) {
        AmbienteDTO updated = ambienteService.updateAmbiente(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
            ambienteService.deleteAmbiente(id);
            return ResponseEntity.ok("Ambiente excluído com sucesso.");
    }
}