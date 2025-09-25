package com.squad13.apimonolito.controllers.catalog;

import com.squad13.apimonolito.DTO.catalog.PadraoDTO;
import com.squad13.apimonolito.DTO.catalog.ComposicaoDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditPadraoDTO;
import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
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
    public ResponseEntity<List<Padrao>> getAll() {
        return ResponseEntity.ok(padraoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Padrao> getById(@PathVariable Long id) {
        return ResponseEntity.ok(padraoService.findByIdOrThrow(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Padrao> getByName(@PathVariable String name) {
        return ResponseEntity.ok(padraoService.findByNameOrThrow(name));
    }

    @GetMapping("/composition/catalogo")
    public ResponseEntity<List<Padrao>> getPadraoByCompositor(@RequestParam Long idCompositor, @RequestParam CompositorEnum compType) {
        return ResponseEntity.ok(composicaoService.findPadraoByCompositor(idCompositor, compType));
    }

    @GetMapping("/{id}/ambiente")
    public ResponseEntity<List<ItemAmbiente>> getItemAmbienteByPadrao(@PathVariable Long id) {
        return ResponseEntity.ok(composicaoService.findItensAmbienteByPadrao(id));
    }

    @GetMapping("/{id}/material")
    public ResponseEntity<List<MarcaMaterial>> getMarcaMaterialByPadrao(@PathVariable Long id) {
        return ResponseEntity.ok(composicaoService.findMarcasMaterialByPadrao(id));
    }

    @PostMapping("/new")
    public ResponseEntity<PadraoDTO> create(@RequestBody @Valid PadraoDTO dto) {
        return ResponseEntity.ok(padraoService.createPadrao(dto));
    }

    @PostMapping("/{id}/rel/single")
    public ResponseEntity<ComposicaoDTO> createSingleComposition(
            @PathVariable Long id,
            @RequestParam Long associationId,
            @RequestParam CompositorEnum compType
    ) {
        return ResponseEntity.ok(composicaoService.addSingleAssociationToPadrao(id, associationId, compType));
    }

    @PostMapping("/{id}/rel/all")
    public ResponseEntity<List<ComposicaoDTO>> createAllCompositions(
            @PathVariable Long id,
            @RequestParam Long associationId,
            @RequestParam CompositorEnum compType
    ) {
        return ResponseEntity.ok(composicaoService.addAllAssociationsToPadrao(id, associationId, compType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PadraoDTO> edit(@PathVariable Long id, @RequestBody @Valid EditPadraoDTO dto) {
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
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(padraoService.deactivatePadrao(id));
    }
}