package com.squad13.apimonolito.controllers.editor;

import com.squad13.apimonolito.DTO.editor.*;
import com.squad13.apimonolito.DTO.editor.edit.EditEspecificacaoDocDTO;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.services.editor.EspecificacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editor/especificacao")
@RequiredArgsConstructor
public class EspecificacaoController {

    private final EspecificacaoService especificacaoService;

    @GetMapping()
    public ResponseEntity<List<EspecificacaoDoc>> findAll() {
        return ResponseEntity.ok(especificacaoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecificacaoDoc> findById(@PathVariable String id) {
        return ResponseEntity.ok(especificacaoService.getById(id));
    }

    @PostMapping("/new")
    public ResponseEntity<EspecificacaoDoc> create(@RequestBody EspecificacaoDocDTO dto){
        return ResponseEntity.ok(especificacaoService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecificacaoDoc> update(@PathVariable String id, @Valid @RequestBody EditEspecificacaoDocDTO dto) {
        return ResponseEntity.ok(especificacaoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        especificacaoService.delete(id);
        return ResponseEntity.ok("Especificação deletada com sucesso");
    }
}