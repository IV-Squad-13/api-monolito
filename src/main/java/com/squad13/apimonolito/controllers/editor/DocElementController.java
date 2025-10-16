package com.squad13.apimonolito.controllers.editor;

import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.DTO.editor.DocElementParams;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.services.editor.EspecificacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
public class DocElementController {

    private final EspecificacaoService especificacaoService;

    @GetMapping
    public ResponseEntity<List<? extends DocElement>> getAll(@ModelAttribute DocElementParams params) {
        return ResponseEntity.ok(especificacaoService.getAll(params));
    }
}
