package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.LoadParametersDTO;
import com.squad13.apimonolito.DTO.catalog.PadraoDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResComposicaoDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditPadraoDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMarcaMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResPadraoDTO;
import com.squad13.apimonolito.services.catalog.PadraoService;
import com.squad13.apimonolito.services.catalog.ComposicaoService;
import com.squad13.apimonolito.util.enums.CompositorEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/padrao")
@RequiredArgsConstructor
public class PadraoController {

    private final PadraoService padraoService;

    private final ComposicaoService composicaoService;

    @GetMapping()
    public ResponseEntity<List<ResPadraoDTO>> getAll(@ModelAttribute LoadParametersDTO loadDTO) {
        return ResponseEntity.ok(padraoService.findAll(loadDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResPadraoDTO> getById(@PathVariable Long id, @ModelAttribute LoadParametersDTO loadDTO) {
        return ResponseEntity.ok(padraoService.findById(id, loadDTO));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ResPadraoDTO> getByName(@PathVariable String name, @ModelAttribute LoadParametersDTO loadDTO) {
        return ResponseEntity.ok(padraoService.findByNameOrThrow(name, loadDTO));
    }

    @GetMapping("/composition/catalogo")
    public ResponseEntity<List<ResPadraoDTO>> getPadraoByCompositor(@RequestParam Long idCompositor, @RequestParam CompositorEnum compType) {
        return ResponseEntity.ok(composicaoService.findPadraoByCompositor(idCompositor, compType));
    }

    @GetMapping("/{id}/ambiente")
    public ResponseEntity<List<ResItemAmbienteDTO>> getItemAmbienteByPadrao(@PathVariable Long id) {
        return ResponseEntity.ok(composicaoService.findItensAmbienteByPadrao(id));
    }

    @GetMapping("/{id}/material")
    public ResponseEntity<List<ResMarcaMaterialDTO>> getMarcaMaterialByPadrao(@PathVariable Long id) {
        return ResponseEntity.ok(composicaoService.findMarcasMaterialByPadrao(id));
    }

    @PostMapping("/new")
    public ResponseEntity<ResPadraoDTO> create(@RequestBody @Valid PadraoDTO dto) {
        return ResponseEntity.ok(padraoService.createPadrao(dto));
    }

    @PostMapping("/{id}/rel/single")
    public ResponseEntity<ResComposicaoDTO> createSingleComposition(
            @PathVariable Long id,
            @RequestParam Long associationId,
            @RequestParam CompositorEnum compType
    ) {
        return ResponseEntity.ok(composicaoService.addSingleAssociationToPadrao(id, associationId, compType));
    }

    @PostMapping("/{id}/rel/all")
    public ResponseEntity<List<ResComposicaoDTO>> createAllCompositions(
            @PathVariable Long id,
            @RequestParam Long associationId,
            @RequestParam CompositorEnum compType
    ) {
        return ResponseEntity.ok(composicaoService.addAllAssociationsToPadrao(id, associationId, compType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResPadraoDTO> edit(@PathVariable Long id, @RequestBody @Valid EditPadraoDTO dto) {
        return ResponseEntity.ok(padraoService.updatePadrao(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        padraoService.deletePadrao(id);
        return ResponseEntity.ok("Catálogo excluído com sucesso.");
    }

    @DeleteMapping("/{id}/rel/single")
    public ResponseEntity<?> deleteComposition(
            @PathVariable Long id,
            @RequestParam CompositorEnum compType
    ) {
        composicaoService.removeSingleCompFromPadrao(id, compType);
        return ResponseEntity.ok("Composições excluídas com sucesso.");
    }

    @DeleteMapping("/{id}/rel/all")
    public ResponseEntity<?> deleteCompositionByCompositor(
            @PathVariable Long id,
            @RequestParam Long compositorId,
            @RequestParam CompositorEnum compType
    ) {
        composicaoService.removeCompositorFromPadrao(id, compositorId, compType);
        return ResponseEntity.ok("Composições excluídas com sucesso.");
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<ResPadraoDTO> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(padraoService.deactivatePadrao(id));
    }
}