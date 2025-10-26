package com.squad13.apimonolito.controllers.revisao;

import com.squad13.apimonolito.DTO.revision.LoadRevDocParamsDTO;
import com.squad13.apimonolito.DTO.revision.StartRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDTO;
import com.squad13.apimonolito.services.revision.RevisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/revision")
@RequiredArgsConstructor
public class RevisionController {

    private final RevisionService revService;

    @GetMapping
    public ResponseEntity<List<ResRevDTO>> getAll(@ModelAttribute LoadRevDocParamsDTO params) {
        return ResponseEntity.ok(revService.findALl(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResRevDTO> getById(@PathVariable Long id, LoadRevDocParamsDTO params) {
        return ResponseEntity.ok(revService.findById(id, params));
    }

    @GetMapping("/emp/{id}")
    public ResponseEntity<ResRevDTO> getByEmpId(@PathVariable Long id, LoadRevDocParamsDTO params) {
        return ResponseEntity.ok(revService.findByEmpreendimentoId(id, params));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ResRevDTO> start(
            @PathVariable Long id,
            @RequestParam Long revisorId,
            @ModelAttribute LoadRevDocParamsDTO params
    ) {
        return ResponseEntity.ok(revService.start(id, revisorId, params));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        revService.delete(id);
        return ResponseEntity.ok("Deletado com sucesso");
    }
}