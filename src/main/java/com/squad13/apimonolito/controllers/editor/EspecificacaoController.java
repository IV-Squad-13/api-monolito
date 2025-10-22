package com.squad13.apimonolito.controllers.editor;

import com.squad13.apimonolito.DTO.editor.*;
import com.squad13.apimonolito.DTO.editor.edit.EditEspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.res.ResSpecDTO;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.services.editor.EspecificacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
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
            @PathVariable ObjectId id
    ) {
        return ResponseEntity.ok(especificacaoService.findById(params, id));
    }

    @PostMapping("/new")
    public ResponseEntity<ResSpecDTO> create(@RequestBody EspecificacaoDocDTO dto) {
        return ResponseEntity.ok(especificacaoService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResSpecDTO> update(@PathVariable ObjectId id, @Valid @RequestBody EditEspecificacaoDocDTO dto) {
        return ResponseEntity.ok(especificacaoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable ObjectId id) {
        especificacaoService.delete(id);
        return ResponseEntity.ok("Especificação deletada com sucesso");
    }
}