package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.EditMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.MaterialDTO;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.services.catalog.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping()
    public List<Material> getAll(){
        return materialService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Material> getById(@PathVariable Long id){
        return materialService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/new")
    public ResponseEntity<MaterialDTO> create(@RequestBody MaterialDTO dto) {
        return ResponseEntity.ok(materialService.createMaterial(dto));
    }


    @PutMapping
    public ResponseEntity<MaterialDTO> update(@RequestBody EditMaterialDTO dto) {
        MaterialDTO updated = materialService.updateMaterial(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
            materialService.deleteMaterial(id);
            return ResponseEntity.ok("Material excluído com sucesso.");
    }
}
