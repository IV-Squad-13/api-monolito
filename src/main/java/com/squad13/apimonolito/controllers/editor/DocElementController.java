package com.squad13.apimonolito.controllers.editor;

import com.squad13.apimonolito.DTO.editor.*;
import com.squad13.apimonolito.DTO.editor.res.ResDocElementDTO;
import com.squad13.apimonolito.services.editor.DocElementService;
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
            @PathVariable String id,
            @ModelAttribute LoadDocumentParamsDTO params,
            @RequestParam DocElementEnum docType
    ) {
        return ResponseEntity.ok(docElementService.getById(new ObjectId(id), params, docType));
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
    public ResponseEntity<ResDocElementDTO> create(@PathVariable String specId, @Valid @RequestBody DocElementCatalogCreationDTO dto) {
        return ResponseEntity.ok(docElementService.createElement(new ObjectId(specId), dto));
    }

    @PostMapping("/{specId}/catalog/bulk")
    @PreAuthorize("@require.editingStage(#specId)")
    public ResponseEntity<Map<DocElementEnum, List<? extends ResDocElementDTO>>> createBulk(@PathVariable String specId, @Valid @RequestBody BulkDocElementCatalogCreationDTO bulkDto) {
        return ResponseEntity.ok(docElementService.createManyElements(new ObjectId(specId), bulkDto));
    }

    @PostMapping("/{specId}/raw")
    @PreAuthorize("@require.editingStage(#specId)")
    public ResponseEntity<ResDocElementDTO> createRawElement(@PathVariable String specId, @Valid @RequestBody DocElementDTO dto) {
        return ResponseEntity.ok(docElementService.createRawElement(new ObjectId(specId), dto));
    }

    // TODO: Criar DTO de edição de documentos
    @PutMapping("/{id}")
    @PreAuthorize("@require.editingStage(#id, #dto.docType)")
    public ResponseEntity<ResDocElementDTO> update(@PathVariable String id, @RequestBody DocElementDTO dto) {
        return ResponseEntity.ok(docElementService.update(new ObjectId(id), dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@require.editingStage(#id, #type)")
    public ResponseEntity<?> delete(@PathVariable String id, @RequestParam DocElementEnum type) {
        docElementService.delete(new ObjectId(id), type);
        return ResponseEntity.ok(type.getDocElement() + "deletado com sucesso");
    }
}
