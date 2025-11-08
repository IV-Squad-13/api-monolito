package com.squad13.apimonolito.controllers.editor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.DTO.editor.*;
import com.squad13.apimonolito.DTO.editor.res.ResDocElementDTO;
import com.squad13.apimonolito.services.editor.DocElementService;
import com.squad13.apimonolito.util.ObjectIdDeserializer;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/editor/document")
@RequiredArgsConstructor
public class DocElementController {

    private final DocElementService docElementService;

    @GetMapping
    public ResponseEntity<List<? extends ResDocElementDTO>> getAll(
            @ModelAttribute LoadDocumentParamsDTO params,
            @RequestParam DocElementEnum docType
    ) {
        return ResponseEntity.ok(docElementService.getAll(params, docType));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResDocElementDTO> getById(
            @PathVariable ObjectId id,
            @ModelAttribute LoadDocumentParamsDTO params,
            @RequestParam DocElementEnum docType
    ) {
        return ResponseEntity.ok(docElementService.getById(id, params, docType));
    }

    @GetMapping("/search")
    public ResponseEntity<List<? extends ResDocElementDTO>> search(
            @ModelAttribute LoadDocumentParamsDTO loadParams,
            @ModelAttribute DocSearchParamsDTO searchParams
    ) {
        return ResponseEntity.ok(docElementService.search(loadParams, searchParams));
    }

    @PostMapping("/{specId}/catalog")
    @PreAuthorize("@require.editingStage(#specId)")
    public ResponseEntity<ResDocElementDTO> create(@PathVariable ObjectId specId, @Valid @RequestBody DocElementCatalogCreationDTO dto) {
        return ResponseEntity.ok(docElementService.createElement(specId, dto));
    }

    @PostMapping("/{specId}/catalog/bulk")
    @PreAuthorize("@require.editingStage(#specId)")
    public ResponseEntity<Map<DocElementEnum, List<? extends ResDocElementDTO>>> createBulk(@PathVariable ObjectId specId, @Valid @RequestBody BulkDocElementCatalogCreationDTO bulkDto) {
        return ResponseEntity.ok(docElementService.createManyElements(specId, bulkDto));
    }

    @PostMapping("/{specId}/raw")
    @PreAuthorize("@require.editingStage(#specId)")
    public ResponseEntity<ResDocElementDTO> createRawElement(@PathVariable ObjectId specId, @Valid @RequestBody DocElementDTO dto) {
        return ResponseEntity.ok(docElementService.createRawElement(specId, dto));
    }

    // TODO: Criar DTO de edição de documentos
    @PutMapping("/{id}")
    @PreAuthorize("@require.editingStage(#id, #dto.docType)")
    public ResponseEntity<ResDocElementDTO> update(@PathVariable ObjectId id, @RequestBody DocElementDTO dto) {
        return ResponseEntity.ok(docElementService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@require.editingStage(#id, #type)")
    public ResponseEntity<?> delete(@PathVariable ObjectId id, @RequestParam DocElementEnum type) {
        docElementService.delete(id, type);
        return ResponseEntity.ok(type.getDocElement() + "deletado com sucesso");
    }
}
