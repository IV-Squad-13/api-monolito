package com.squad13.apimonolito.controllers;

import com.squad13.apimonolito.dto.res.SpecResDTO;
import com.squad13.apimonolito.services.CatalogoService;
import com.squad13.apimonolito.util.enums.CatalogSpecEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/{spec}")
    public ResponseEntity<List<SpecResDTO>> getSpecTable(@PathVariable String spec) {
        CatalogSpecEnum enumSpec = CatalogSpecEnum.fromString(spec);
        return ResponseEntity.ok(catalogoService.findAllBySpec(enumSpec));
    }
}
