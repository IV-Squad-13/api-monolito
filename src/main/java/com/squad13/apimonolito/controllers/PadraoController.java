package com.squad13.apimonolito.controllers;

import com.squad13.apimonolito.models.Padrao;
import com.squad13.apimonolito.services.PadraoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/padroes")
public class PadraoController {

    @Autowired
    private PadraoService padraoService;

    @GetMapping
    public List<Padrao> getAll() {
        return padraoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Padrao> getById(@PathVariable Long id) {
        return padraoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}