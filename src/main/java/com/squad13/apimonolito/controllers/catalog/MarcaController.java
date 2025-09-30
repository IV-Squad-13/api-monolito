package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.edit.EditMarcaDTO;
import com.squad13.apimonolito.DTO.catalog.MarcaDTO;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import com.squad13.apimonolito.services.catalog.MarcaMaterialService;
import com.squad13.apimonolito.services.catalog.MarcaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/marca")
public class MarcaController {

    @Autowired
    private MarcaService marcaService;

    @Autowired
    private MarcaMaterialService marcaMaterialService;

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

    @GetMapping("/search")
    public ResponseEntity<List<Marca>> getByAttribute(@RequestParam String attribute, @RequestParam String value) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(marcaService.findByAttribute(attribute, value));
    }

    @GetMapping("/rel")
    public ResponseEntity<List<Material>> getAssociations(@RequestParam(required = false) Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(marcaMaterialService.findMarcaMaterials(id));
    }

    @PostMapping
    public ResponseEntity<MarcaDTO> create(@RequestBody @Valid MarcaDTO dto) {
            MarcaDTO created = marcaService.createMarca(dto);
            return ResponseEntity.ok(created);
    }

    @PostMapping("/{id}/material")
    public ResponseEntity<MarcaMaterial> addAssociation(@PathVariable Long id, @RequestParam Long materialId) {
        return ResponseEntity.ok(marcaMaterialService.associateMarcaAndMaterial(id, materialId));
    }

    @PutMapping
    public ResponseEntity<MarcaDTO> update(@RequestBody @Valid EditMarcaDTO dto) {
            MarcaDTO updated = marcaService.updateMarca(dto);
            return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
            marcaService.deleteMarca(id);
            return ResponseEntity.ok("Marca excluída com sucesso.");
    }

    @DeleteMapping("/{id}/material")
    public ResponseEntity<?> deleteAssociation(@PathVariable Long id, @RequestParam Long materialId) {
        marcaMaterialService.deleteMarcaAndMaterialAssociation(id, materialId);
        return ResponseEntity.ok("Marca removida do material com sucesso.");
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        EditMarcaDTO dto = new EditMarcaDTO();
        dto.setId(id);
        dto.setIsActive(false);

        return ResponseEntity.status(HttpStatus.OK)
                .body(marcaService.updateMarca(dto));
    }
}
