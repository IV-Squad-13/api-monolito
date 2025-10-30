package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditItemTypeDTO;
import com.squad13.apimonolito.DTO.catalog.ItemTypeDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemTypeDTO;
import com.squad13.apimonolito.models.catalog.ItemType;
import com.squad13.apimonolito.services.catalog.ItemAmbienteService;
import com.squad13.apimonolito.services.catalog.ItemTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogo/item-types")
@RequiredArgsConstructor
public class ItemTypeController {

    private final ItemTypeService itemTypeService;
    private final ItemAmbienteService itemAmbienteService;

    @GetMapping
    public ResponseEntity<List<ResItemTypeDTO>> getAll() {
        return ResponseEntity.ok(itemTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResItemTypeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(itemTypeService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ResItemTypeDTO> getByName(@PathVariable String name) {
        return ResponseEntity.ok(itemTypeService.findByNameOrThrow(name));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResItemTypeDTO>> search(
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

        return ResponseEntity.ok(itemTypeService.findByFilters(filters, loadDTO));
    }

    @GetMapping("/rel")
    public ResponseEntity<List<ResAmbienteDTO>> getAssociations(@RequestParam(required = false) Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemAmbienteService.findItemTypeAmbientes(id));
    }

    @PostMapping("/new")
    public ResponseEntity<ResItemTypeDTO> create(@RequestBody @Valid ItemTypeDTO dto) {
        return ResponseEntity.ok(itemTypeService.createItemType(dto));
    }

    @PostMapping("/{id}/ambiente")
    public ResponseEntity<List<ResItemAmbienteDTO>> addAssociation(@PathVariable Long id, @RequestParam Long ambienteId) {
        return ResponseEntity.ok(itemAmbienteService.associateItemTypeToAmbiente(id, ambienteId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResItemTypeDTO> edit(@PathVariable Long id, @RequestBody @Valid EditItemTypeDTO dto) {
        return ResponseEntity.ok(itemTypeService.updateItemType(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        itemTypeService.deleteItemType(id);
        return ResponseEntity.ok("ItemType excluído com sucesso.");
    }

    @DeleteMapping("/{id}/ambiente")
    public ResponseEntity<?> deleteAssociation(@PathVariable Long id, @RequestParam Long ambienteId) {
        itemAmbienteService.deleteItemAndAmbienteAssociationsByItemType(id, ambienteId);
        return ResponseEntity.ok("Itens removido do ambiente com sucesso.");
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ResItemTypeDTO> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(itemTypeService.deactivateItemType(id));
    }
}