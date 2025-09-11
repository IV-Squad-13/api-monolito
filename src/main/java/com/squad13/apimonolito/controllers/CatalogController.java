package com.squad13.apimonolito.controllers;

import com.squad13.apimonolito.dto.req.SpecReqDTO;
import com.squad13.apimonolito.dto.res.SpecResDTO;
import com.squad13.apimonolito.services.CatalogService;
import com.squad13.apimonolito.util.enums.CatalogSpecEnum;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/{spec}")
    public ResponseEntity<List<SpecResDTO>> getSpecTable(@PathVariable String spec) {
        CatalogSpecEnum enumSpec = CatalogSpecEnum.fromString(spec);
        return ResponseEntity.ok(catalogService.findAllBySpec(enumSpec));
    }

    @GetMapping("/{spec}/single")
    public ResponseEntity<SpecResDTO> getSingleSpec(@PathVariable String spec, @RequestParam(required = false) Optional<Long> id, @RequestParam(required = false) Optional<String> name) {
        CatalogSpecEnum enumSpec = CatalogSpecEnum.fromString(spec);
        return ResponseEntity.ok(catalogService.findSingleSpec(enumSpec, id, name));
    }

    @PostMapping("/{spec}")
    public ResponseEntity<?> createSpec(@PathVariable String spec, @RequestBody @Valid SpecReqDTO reqDTO) {
        CatalogSpecEnum enumSpec = CatalogSpecEnum.fromString(spec);
        return ResponseEntity.created(catalogService.createSpec(enumSpec, reqDTO)).build();
    }
}
