package com.squad13.apimonolito.controllers.editor;

import com.squad13.apimonolito.DTO.editor.EspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.EspecificacaoSearchParamsDTO;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.edit.EditEspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.res.ResSpecDTO;
import com.squad13.apimonolito.services.editor.EspecificacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editor/especificacao")
@RequiredArgsConstructor
public class EspecificacaoController {

    private final EspecificacaoService especificacaoService;

    @GetMapping()
    public ResponseEntity<List<ResSpecDTO>> getAll(@ModelAttribute LoadDocumentParamsDTO params) {
        return ResponseEntity.ok(especificacaoService.findAll(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResSpecDTO> getById(
            @ModelAttribute LoadDocumentParamsDTO params,
            @PathVariable String id
    ) {
        return ResponseEntity.ok(especificacaoService.findById(new ObjectId(id), params));
    }

    @GetMapping("/emp/{empId}")
    public ResponseEntity<ResSpecDTO> getByEmpId(
            @ModelAttribute LoadDocumentParamsDTO params,
            @PathVariable Long empId
    ) {
        return ResponseEntity.ok(especificacaoService.findByEmpId(empId, params));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResSpecDTO>> search(
            @ModelAttribute LoadDocumentParamsDTO loadParams,
            @ModelAttribute EspecificacaoSearchParamsDTO searchParams
    ) {
        return ResponseEntity.ok(especificacaoService.search(loadParams, searchParams));
    }

    @PostMapping("/new")
    public ResponseEntity<ResSpecDTO> create(@RequestBody EspecificacaoDocDTO dto) {
        return ResponseEntity.ok(especificacaoService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@require.editingStage(#id)")
    public ResponseEntity<ResSpecDTO> update(@PathVariable String id, @Valid @RequestBody EditEspecificacaoDocDTO dto) {
        return ResponseEntity.ok(especificacaoService.update(new ObjectId(id), dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@require.editingStage(#id)")
    public ResponseEntity<?> delete(@PathVariable String id) {
        especificacaoService.delete(new ObjectId(id));
        return ResponseEntity.ok("Especificação deletada com sucesso");
    }
}