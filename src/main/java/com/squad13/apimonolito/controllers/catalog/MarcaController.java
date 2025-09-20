package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.EditMarcaDTO;
import com.squad13.apimonolito.DTO.catalog.MarcaDTO;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.services.catalog.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/marca")
public class MarcaController {

    @Autowired
    private MarcaService marcaService;

    @GetMapping
    public List<Marca> getAll(){
        return marcaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marca> getById(@PathVariable Long id){
        return marcaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MarcaDTO> create(@RequestBody MarcaDTO dto) {
            MarcaDTO created = marcaService.createMarca(dto);
            return ResponseEntity.ok(created);
    }

    @PutMapping
    public ResponseEntity<MarcaDTO> update(@RequestBody EditMarcaDTO dto) {
            MarcaDTO updated = marcaService.updateMarca(dto);
            return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
            marcaService.deleteMarca(id);
            return ResponseEntity.ok("Marca excluída com sucesso.");
    }
}
