package com.squad13.apimonolito.controllers;

import com.squad13.apimonolito.models.TipoItem;
import com.squad13.apimonolito.repository.TipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipo")
public class TipoItemController {

    @Autowired
    private TipoRepository tipoRepository;

    @GetMapping()
    public List<TipoItem> getAll(){
        return tipoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoItem> getById(@PathVariable Long id){
        return tipoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
