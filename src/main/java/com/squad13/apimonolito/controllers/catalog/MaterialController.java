package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.edit.EditMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.MaterialDTO;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import com.squad13.apimonolito.services.catalog.MarcaMaterialService;
import com.squad13.apimonolito.services.catalog.MaterialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MarcaMaterialService marcaMaterialService;

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

    @GetMapping("/search")
    public ResponseEntity<List<Material>> getByAttribute(@RequestParam String attribute, @RequestParam String value) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialService.findByAttribute(attribute, value));
    }

    @GetMapping("/rel")
    public ResponseEntity<List<Marca>> getAssociations(@RequestParam(required = false) Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(marcaMaterialService.findMaterialMarcas(id));
    }

    @PostMapping("/new")
    public ResponseEntity<MaterialDTO> create(@RequestBody @Valid MaterialDTO dto) {
        return ResponseEntity.ok(materialService.createMaterial(dto));
    }

    @PostMapping("/{id}/marca")
    public ResponseEntity<MarcaMaterial> addAssociation(@PathVariable Long id, @RequestParam Long marcaId) {
        return ResponseEntity.ok(marcaMaterialService.associateMarcaAndMaterial(marcaId, id));
    }

    @PutMapping
    public ResponseEntity<MaterialDTO> update(@RequestBody @Valid EditMaterialDTO dto) {
        MaterialDTO updated = materialService.updateMaterial(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
            materialService.deleteMaterial(id);
            return ResponseEntity.ok("Material excluído com sucesso.");
    }

    @DeleteMapping("/{id}/marca")
    public ResponseEntity<?> deleteAssociation(@PathVariable Long id, @RequestParam Long marcaId) {
        marcaMaterialService.deleteMarcaAndMaterialAssociation(marcaId, id);
        return ResponseEntity.ok("Marca removida do material com sucesso.");
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        EditMaterialDTO dto = new EditMaterialDTO();
        dto.setId(id);
        dto.setIsActive(false);

        return ResponseEntity.status(HttpStatus.OK)
                .body(materialService.updateMaterial(dto));
    }
}
