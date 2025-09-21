package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.CatalogoDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditCatalogoDTO;
import com.squad13.apimonolito.models.catalog.Catalogo;
import com.squad13.apimonolito.services.catalog.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping()
    public ResponseEntity<List<Catalogo>> getAll() {
        return ResponseEntity.ok(catalogoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Catalogo> getById(@PathVariable Long id) {
        return catalogoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Catalogo> getByName(@RequestParam String name) {
        return catalogoService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/new")
    public ResponseEntity<CatalogoDTO> create(@RequestBody @Valid CatalogoDTO dto) {
        return ResponseEntity.ok(catalogoService.createCatalogo(dto));
    }

    @PutMapping
    public ResponseEntity<CatalogoDTO> edit(@RequestBody @Valid EditCatalogoDTO dto) {
        CatalogoDTO update = catalogoService.updateCatalogo(dto);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        catalogoService.deleteCatalogo(id);
        return ResponseEntity.ok("Catálogo excluído com sucesso.");
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        EditCatalogoDTO dto = new EditCatalogoDTO();
        dto.setId(id);
        dto.setIsActive(false);

        return ResponseEntity.status(HttpStatus.OK)
                .body(catalogoService.updateCatalogo(dto));
    }
}