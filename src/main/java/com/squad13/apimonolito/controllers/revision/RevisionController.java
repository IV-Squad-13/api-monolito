package com.squad13.apimonolito.controllers.revision;

import com.squad13.apimonolito.DTO.revision.LoadRevDocParamsDTO;
import com.squad13.apimonolito.DTO.revision.RevDocSearchParamsDTO;
import com.squad13.apimonolito.DTO.revision.ToRevisionDTO;
import com.squad13.apimonolito.DTO.revision.edit.EditRevDocDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDocDTO;
import com.squad13.apimonolito.DTO.revision.res.ResSpecRevDTO;
import com.squad13.apimonolito.services.editor.EmpreendimentoService;
import com.squad13.apimonolito.services.pdf.PdfGeneratorService;
import com.squad13.apimonolito.services.revision.ApprovalService;
import com.squad13.apimonolito.services.revision.RevisionService;
import com.squad13.apimonolito.util.enums.RevDocElementEnum;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/revision")
@RequiredArgsConstructor
public class RevisionController {

    private final EmpreendimentoService empService;
    private final RevisionService revService;
    private final ApprovalService approvalService;
    private final PdfGeneratorService pdfGeneratorService;

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

    @GetMapping("/doc/{id}/pdf")
    public ResponseEntity<byte[]> getDocPdfById(
            @PathVariable String id,
            @ModelAttribute LoadRevDocParamsDTO params,
            @RequestParam RevDocElementEnum docType
    ) {
        ResRevDocDTO doc = revService.findDocById(new ObjectId(id), params, docType);

        String titulo = "Documento de Revisão - " + id;
        String conteudo = doc.toString();


        byte[] pdfBytes = pdfGeneratorService.gerarPdfSimples(titulo, conteudo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "attachment",
                "documento-revisao-" + id + ".pdf"
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/doc/search")
    public ResponseEntity<List<? extends ResRevDocDTO>> searchDocs(
            @ModelAttribute LoadRevDocParamsDTO loadParams,
            @ModelAttribute RevDocSearchParamsDTO searchParams
    ) {
        return ResponseEntity.ok(revService.searchDocs(loadParams, searchParams, false));
    }

    @GetMapping("/emp/{id}")
    public ResponseEntity<ResRevDTO> getByEmpId(@PathVariable Long id, @ModelAttribute LoadRevDocParamsDTO params) {
        return ResponseEntity.ok(revService.findByEmpreendimentoId(id, params));
    }

    @PostMapping("/{id}/request")
    @PreAuthorize("@require.editingStage(#id)")
    public ResponseEntity<ResRevDTO> toRevision(@PathVariable Long id, @RequestBody ToRevisionDTO dto) {
        return ResponseEntity.ok(empService.requestRevision(id, dto));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("@require.pendingRevisionStage(#id)")
    public ResponseEntity<ResRevDTO> start(
            @PathVariable Long id,
            @RequestParam Long revisorId,
            @ModelAttribute LoadRevDocParamsDTO params
    ) {
        return ResponseEntity.ok(revService.start(id, revisorId, params));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("@require.revisionStage(#id)")
    public ResponseEntity<?> reject(
            @PathVariable Long id
    ) {
        approvalService.reject(id);
        return ResponseEntity.ok("Revisão rejeitada! Processo de elaboração reaberto");
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("@require.revisionStage(#id)")
    public ResponseEntity<?> approve(
            @PathVariable Long id
    ) {
        approvalService.approve(id);
        return ResponseEntity.ok("Revisão Aprovada! Processo de elaboração concluído");
    }

    @PutMapping("/doc/{id}")
    @PreAuthorize("@require.revisionStage(#id, #dto.docType)")
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