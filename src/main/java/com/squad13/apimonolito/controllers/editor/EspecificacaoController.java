package com.squad13.apimonolito.controllers.editor;

import com.squad13.apimonolito.DTO.editor.CatalogRelationDTO;
import com.squad13.apimonolito.DTO.editor.LocalDocDTO;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.services.editor.EspecificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editor/especificacao")
@RequiredArgsConstructor
public class EspecificacaoController {

    private final EspecificacaoService especService;

    @GetMapping()
    public ResponseEntity<List<EspecificacaoDoc>> findAll() {
        return ResponseEntity.ok(especService.getAll());
    }

    @PostMapping("/new")
    public ResponseEntity<EspecificacaoDoc> create(@RequestParam Long empId){
        return ResponseEntity.ok(especService.create(empId));
    }

    @PostMapping("/{id}/add-element-from-catalog")
    public ResponseEntity<EspecificacaoDoc> createElementFromCatalog(@PathVariable String id, @RequestBody CatalogRelationDTO dto) {
        return ResponseEntity.ok(especService.addElementFromCatalog(id, dto));
    }
}