package com.squad13.apimonolito.controllers.catalog;


import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.services.AmbienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ambientes")
public class AmbienteController {

    @Autowired
    private AmbienteService ambienteService;


    @GetMapping
    public List<Ambiente> getAll() {
        return ambienteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ambiente> getById(@PathVariable Long id) {
        return ambienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}