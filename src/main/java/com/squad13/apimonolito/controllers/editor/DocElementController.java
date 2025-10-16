package com.squad13.apimonolito.controllers.editor;

import com.squad13.apimonolito.DTO.editor.DocElementCatalogCreationDTO;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.DTO.editor.DocElementParams;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.services.editor.DocElementService;
import com.squad13.apimonolito.services.editor.EspecificacaoService;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
public class DocElementController {

    private final DocElementService docElementService;

    @GetMapping
    public ResponseEntity<List<? extends DocElement>> getAll(@ModelAttribute DocElementParams params) {
        return ResponseEntity.ok(docElementService.getAll(params));
    }

    @PostMapping("/{especificacaoId}/catalog")
    public ResponseEntity<DocElement> create(@PathVariable String especificacaoId, @Valid @RequestBody DocElementCatalogCreationDTO dto) {
        return ResponseEntity.ok(docElementService.createElement(especificacaoId, dto));
    }

    @PostMapping("/{especificacaoId}/raw")
    public ResponseEntity<DocElement> createRawElement(@PathVariable String especificacaoId, @Valid @RequestBody DocElementDTO dto) {
        return ResponseEntity.ok(docElementService.createRawElement(especificacaoId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocElement> update(@PathVariable String id, @Valid @RequestBody DocElementDTO dto) {
        return ResponseEntity.ok(docElementService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, @RequestParam DocElementEnum type) {
        docElementService.delete(id, type);
        return ResponseEntity.ok(type.getDocElement() + "deletado com sucesso");
    }
}
