package com.squad13.apimonolito.controllers;

import com.squad13.apimonolito.models.Padrao;
import com.squad13.apimonolito.repositories.PadraoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/padrao")
public class PadraoController {

    private PadraoRepository padraoRepository;

    public PadraoController(PadraoRepository padraoRepository) {
        this.padraoRepository = padraoRepository;
    }

    @GetMapping
    public List<Padrao> findAll() {
        return padraoRepository.findAll();
    }

    @PostMapping
    public Padrao save(@RequestBody String nome) {
        Padrao padrao = new Padrao();
        padrao.setNome(nome);
        return padraoRepository.save(padrao);
    }
}