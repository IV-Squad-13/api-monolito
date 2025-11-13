package com.squad13.apimonolito.controllers.editor;

import com.squad13.apimonolito.DTO.editor.EspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.EspecificacaoSearchParamsDTO;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.edit.EditEspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.res.ResSpecDTO;
import com.squad13.apimonolito.services.editor.EspecificacaoService;
import com.squad13.apimonolito.services.pdf.PdfGeneratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editor/especificacao")
@RequiredArgsConstructor
public class EspecificacaoController {

    private final EspecificacaoService especificacaoService;
    private final PdfGeneratorService pdfGeneratorService;

    @GetMapping()
    public ResponseEntity<List<ResSpecDTO>> getAll(@ModelAttribute LoadDocumentParamsDTO params) {
        return ResponseEntity.ok(especificacaoService.findAll(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResSpecDTO> getById(
            @ModelAttribute LoadDocumentParamsDTO params,
            @PathVariable ObjectId id
    ) {
        return ResponseEntity.ok(especificacaoService.findById(id, params));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getByIdPdf(
            @ModelAttribute LoadDocumentParamsDTO params,
            @PathVariable ObjectId id
    ) {
        ResSpecDTO spec = especificacaoService.findById(id, params);

        String titulo = "Especificação - " + id;
        String conteudo = spec.toString();

        byte[] pdfBytes = pdfGeneratorService.gerarPdfSimples(titulo, conteudo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "attachment",
                "especificacao-" + id + ".pdf"
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
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
    public ResponseEntity<ResSpecDTO> update(@PathVariable ObjectId id, @Valid @RequestBody EditEspecificacaoDocDTO dto) {
        return ResponseEntity.ok(especificacaoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@require.editingStage(#id)")
    public ResponseEntity<?> delete(@PathVariable ObjectId id) {
        especificacaoService.delete(id);
        return ResponseEntity.ok("Especificação deletada com sucesso");
    }
}