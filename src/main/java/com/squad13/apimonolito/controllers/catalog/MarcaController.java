package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.LoadParametersDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditMarcaDTO;
import com.squad13.apimonolito.DTO.catalog.MarcaDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMarcaDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMarcaMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMaterialDTO;
import com.squad13.apimonolito.services.catalog.MarcaMaterialService;
import com.squad13.apimonolito.services.catalog.MarcaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/marca")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService marcaService;
    private final MarcaMaterialService marcaMaterialService;

    @GetMapping
    public ResponseEntity<List<ResMarcaDTO>> getAll(@ModelAttribute LoadParametersDTO loadDTO) {
        return ResponseEntity.ok(marcaService.findAll(loadDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResMarcaDTO> getById(
            @PathVariable Long id,
            @ModelAttribute("loadAll") LoadParametersDTO loadDTO
    ){
        return ResponseEntity.ok(marcaService.findById(id, loadDTO));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResMarcaDTO>> getByAttribute(
            @RequestParam String attribute,
            @RequestParam String value,
            @ModelAttribute LoadParametersDTO loadDTO
    ) {
        return ResponseEntity.ok(marcaService.findByAttribute(attribute, value, loadDTO));
    }

    @GetMapping("/rel")
    public ResponseEntity<List<ResMaterialDTO>> getAssociations(@RequestParam(required = false) Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(marcaMaterialService.findMarcaMaterials(id));
    }

    @PostMapping("/new")
    public ResponseEntity<ResMarcaDTO> create(@RequestBody @Valid MarcaDTO dto) {
            return ResponseEntity.ok(marcaService.createMarca(dto));
    }

    @PostMapping("/{id}/material")
    public ResponseEntity<ResMarcaMaterialDTO> addAssociation(@PathVariable Long id, @RequestParam Long materialId) {
        return ResponseEntity.ok(marcaMaterialService.associateMarcaAndMaterial(id, materialId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResMarcaDTO> update(@PathVariable Long id, @RequestBody @Valid EditMarcaDTO dto) {
            return ResponseEntity.ok(marcaService.updateMarca(id, dto));
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
    public ResponseEntity<ResMarcaDTO> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(marcaService.deactivateMarca(id));
    }
}
