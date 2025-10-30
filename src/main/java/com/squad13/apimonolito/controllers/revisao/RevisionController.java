package com.squad13.apimonolito.controllers.revisao;

import com.squad13.apimonolito.DTO.revision.LoadRevDocParamsDTO;
import com.squad13.apimonolito.DTO.revision.RevDocSearchParamsDTO;
import com.squad13.apimonolito.DTO.revision.edit.EditRevDocDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDocDTO;
import com.squad13.apimonolito.DTO.revision.res.ResSpecRevDTO;
import com.squad13.apimonolito.services.revision.ApprovalService;
import com.squad13.apimonolito.services.revision.RevisionService;
import com.squad13.apimonolito.util.enums.RevDocElementEnum;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/revision")
@RequiredArgsConstructor
public class RevisionController {

    private final RevisionService revService;
    private final ApprovalService approvalService;

    @GetMapping
    public ResponseEntity<List<ResRevDTO>> getAll(@ModelAttribute LoadRevDocParamsDTO params) {
        return ResponseEntity.ok(revService.findAll(params));
    }

    @GetMapping("/doc")
    public ResponseEntity<List<ResSpecRevDTO>> getAllDocs(@ModelAttribute LoadRevDocParamsDTO params) {
        return ResponseEntity.ok(revService.findAllDocs(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResRevDTO> getById(@PathVariable Long id, @ModelAttribute LoadRevDocParamsDTO params) {
        return ResponseEntity.ok(revService.findById(id, params));
    }

    @GetMapping("/doc/{id}")
    public ResponseEntity<? extends ResRevDocDTO> getDocById(
            @PathVariable String id,
            @ModelAttribute LoadRevDocParamsDTO params,
            @RequestParam RevDocElementEnum docType
    ) {
        return ResponseEntity.ok(revService.findDocById(new ObjectId(id), params, docType));
    }

    @GetMapping("/doc/search")
    public ResponseEntity<List<? extends ResRevDocDTO>> searchDocs(
            @ModelAttribute LoadRevDocParamsDTO loadParams,
            @ModelAttribute RevDocSearchParamsDTO searchParams
    ) {
        return ResponseEntity.ok(revService.searchDocs(loadParams, searchParams));
    }

    @GetMapping("/emp/{id}")
    public ResponseEntity<ResRevDTO> getByEmpId(@PathVariable Long id, @ModelAttribute LoadRevDocParamsDTO params) {
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

    @PutMapping("/doc/{id}")
    public ResponseEntity<? extends ResRevDocDTO> update(
            @PathVariable String id,
            @RequestBody EditRevDocDTO dto
    ) {
        return ResponseEntity.ok(approvalService.update(new ObjectId(id), dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        revService.delete(id);
        return ResponseEntity.ok("Deletado com sucesso");
    }
}