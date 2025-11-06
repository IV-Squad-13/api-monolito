package com.squad13.apimonolito.controllers.editor;

import com.squad13.apimonolito.DTO.editor.EmpDTO;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.edit.EditEmpDTO;
import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
import com.squad13.apimonolito.DTO.revision.ToRevisionDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDTO;
import com.squad13.apimonolito.services.editor.EmpreendimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/editor/empreendimento")
@RestController
public class EmpreendimentoController {

    private final EmpreendimentoService empService;

    @GetMapping()
    public ResponseEntity<List<ResEmpDTO>> getAll(@ModelAttribute LoadDocumentParamsDTO loadDTO) {
        return ResponseEntity.ok(empService.findAll(loadDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResEmpDTO> getById(@PathVariable Long id, @ModelAttribute("loadAll") LoadDocumentParamsDTO loadDTO) {
        return ResponseEntity.ok(empService.findById(id, loadDTO));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResEmpDTO>> getByAttribute(
            @RequestParam String attribute,
            @RequestParam String value,
            @ModelAttribute LoadDocumentParamsDTO loadDTO
    ) {
        return ResponseEntity.ok(empService.findByAttribute(attribute, value, loadDTO));
    }

    @PostMapping("/new")
    public ResponseEntity<ResEmpDTO> create(@RequestBody @Valid EmpDTO dto, @ModelAttribute LoadDocumentParamsDTO loadDTO) {
        return ResponseEntity.ok(empService.create(dto, loadDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResEmpDTO> edit(
            @PathVariable Long id,
            @RequestBody @Valid EditEmpDTO dto,
            @ModelAttribute LoadDocumentParamsDTO loadDTO
    ) {
        return ResponseEntity.ok(empService.update(id, dto, loadDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        empService.delete(id);
        return ResponseEntity.ok("Empreendimento excluído com sucesso.");
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<ResEmpDTO> deactivate(@PathVariable Long id, @ModelAttribute LoadDocumentParamsDTO loadDTO) {
        return ResponseEntity.ok(empService.deactivate(id, loadDTO));
    }
}