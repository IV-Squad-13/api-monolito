package com.squad13.apimonolito.controllers.catalog;


import com.squad13.apimonolito.DTO.catalog.AmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemTypeDTO;
import com.squad13.apimonolito.services.catalog.AmbienteService;
import com.squad13.apimonolito.services.catalog.ItemAmbienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogo/ambiente")
@RequiredArgsConstructor
public class AmbienteController {

    private final AmbienteService ambienteService;
    private final ItemAmbienteService itemAmbienteService;

    @GetMapping
    public ResponseEntity<List<ResAmbienteDTO>> getAll(@ModelAttribute LoadCatalogParamsDTO params) {
        return ResponseEntity.ok(ambienteService.findAll(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResAmbienteDTO> getById(@PathVariable Long id, @ModelAttribute("loadAll") LoadCatalogParamsDTO loadDTO) {
        return ResponseEntity.ok(ambienteService.findById(id, loadDTO));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResAmbienteDTO>> search(
            @RequestParam Map<String, String> filters,
            @ModelAttribute LoadCatalogParamsDTO loadDTO
            ) {

            filters.remove("loadAll");
            filters.remove("loadPadroes");
            filters.remove("loadAmbientes");
            filters.remove("loadItems");
            filters.remove("loadMateriais");
            filters.remove("loadMarcas");
            filters.remove("loadNested");

        return ResponseEntity.ok(ambienteService.findByFilters(filters, loadDTO));
        }

    @GetMapping("/rel")
    public ResponseEntity<List<ResItemDTO>> getAssociations(@RequestParam(required = false) Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemAmbienteService.findAmbienteItems(id));
    }

    @GetMapping("/rel/item-type")
    public ResponseEntity<List<ResItemTypeDTO>> getAssociatedItemTypes(@RequestParam(required = false) Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemAmbienteService.findAmbienteItemTypes(id));
    }

    @PostMapping("/new")
    public ResponseEntity<ResAmbienteDTO> create(@RequestBody @Valid AmbienteDTO dto) {
        return ResponseEntity.ok(ambienteService.createAmbiente(dto));
    }

    @PostMapping("/{id}/item")
    public ResponseEntity<ResItemAmbienteDTO> addAssociation(@PathVariable Long id, @RequestParam Long itemId) {
        return ResponseEntity.ok(itemAmbienteService.associateItemAndAmbiente(itemId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResAmbienteDTO> edit(@PathVariable Long id, @RequestBody @Valid EditAmbienteDTO dto) {
        return ResponseEntity.ok(ambienteService.updateAmbiente(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
            ambienteService.deleteAmbiente(id);
            return ResponseEntity.ok("Ambiente excluído com sucesso.");
    }

    @DeleteMapping("/{id}/item")
    public ResponseEntity<?> deleteAssociation(@PathVariable Long id, @RequestParam Long itemId) {
        itemAmbienteService.deleteItemAndAmbienteAssociation(itemId, id);
        return ResponseEntity.ok("Item removido do ambiente com sucesso.");
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<ResAmbienteDTO> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(ambienteService.deactivateAmbiente(id));
    }
}