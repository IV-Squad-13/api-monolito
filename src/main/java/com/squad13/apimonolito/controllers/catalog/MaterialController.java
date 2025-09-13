package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.services.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping()
    public List<Material> getAll(){
        return materialService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Material> getById(@PathVariable Long id){
        return materialService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
