package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.MaterialDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMarcaDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMarcaMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMaterialDTO;
import com.squad13.apimonolito.services.catalog.MarcaMaterialService;
import com.squad13.apimonolito.services.catalog.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/material")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;
    private final MarcaMaterialService marcaMaterialService;

    @GetMapping()
    public List<ResMaterialDTO> getAll(@ModelAttribute LoadCatalogParamsDTO loadDTO) {
        return materialService.findAll(loadDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResMaterialDTO> getById(@PathVariable Long id, @ModelAttribute("loadAll") LoadCatalogParamsDTO loadDTO) {
        return ResponseEntity.ok(materialService.findById(id, loadDTO));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResMaterialDTO>> getByAttribute(
            @RequestParam String attribute,
            @RequestParam String value,
            @ModelAttribute LoadCatalogParamsDTO loadDTO
    ) {
        return ResponseEntity.ok(materialService.findByAttribute(attribute, value, loadDTO));
    }

    @GetMapping("/rel")
    public ResponseEntity<List<ResMarcaDTO>> getAssociations(@RequestParam(required = false) Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(marcaMaterialService.findMaterialMarcas(id));
    }

    @PostMapping("/new")
    public ResponseEntity<ResMaterialDTO> create(@RequestBody @Valid MaterialDTO dto) {
        return ResponseEntity.ok(materialService.createMaterial(dto));
    }

    @PostMapping("/{id}/marca")
    public ResponseEntity<ResMarcaMaterialDTO> addAssociation(@PathVariable Long id, @RequestParam Long marcaId) {
        return ResponseEntity.ok(marcaMaterialService.associateMarcaAndMaterial(marcaId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResMaterialDTO> update(@PathVariable Long id, @RequestBody @Valid EditMaterialDTO dto) {
        return ResponseEntity.ok(materialService.updateMaterial(id, dto));
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
    public ResponseEntity<ResMaterialDTO> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(materialService.deactivateMaterial(id));
    }
}
