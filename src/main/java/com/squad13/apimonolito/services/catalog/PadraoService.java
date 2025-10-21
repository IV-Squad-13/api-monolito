package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.EditPadraoDTO;
import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.PadraoDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMarcaMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResPadraoDTO;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.repository.catalog.PadraoRepository;
import com.squad13.apimonolito.util.mappers.CatalogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PadraoService {

    private final PadraoRepository padraoRepository;

    private final CatalogMapper catalogMapper;
    private final ComposicaoService composicaoService;

    public List<ResPadraoDTO> findAll(LoadCatalogParamsDTO params) {
        return padraoRepository.findAll()
                .stream()
                .map(padrao -> catalogMapper.toResponse(padrao, params))
                .toList();
    }

    private Padrao findByIdOrThrow(Long id) {
        return padraoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Padrão com id " + id + " não encontrado."));
    }

    public ResPadraoDTO findById(Long id, LoadCatalogParamsDTO params) {
        return padraoRepository.findById(id)
                .map(padrao -> catalogMapper.toResponse(padrao, params))
                .orElseThrow(() -> new ResourceNotFoundException("Padrão com id " + id + " não encontrado."));
    }

    public ResPadraoDTO findByNameOrThrow(String name, LoadCatalogParamsDTO params) {
        return padraoRepository.findByName(name)
                .map(padrao -> catalogMapper.toResponse(padrao, params))
                .orElseThrow(() -> new ResourceNotFoundException("Padrão com nome " + name + " não encontrado."));
    }

    public List<ResItemAmbienteDTO> findItensAmbienteByPadrao(Long id) {
        return composicaoService.findItensAmbienteByPadrao(id).stream()
                .map(catalogMapper::toResponse)
                .toList();
    }

    public List<ResMarcaMaterialDTO> findMarcasMaterialByPadrao(Long id) {
        return composicaoService.findMarcasMaterialByPadrao(id).stream()
                .map(catalogMapper::toResponse)
                .toList();
    }

    public ResPadraoDTO createPadrao(PadraoDTO dto) {
        padraoRepository.findByName(dto.getName())
                .ifPresent(a -> {
                    throw new ResourceAlreadyExistsException(
                            "Já existe um padrão com o nome " + dto.getName()
                    );
                });

        Padrao padrao = new Padrao();
        padrao.setName(dto.getName());
        padrao.setIsActive(dto.isActive());

        return catalogMapper.toResponse(padraoRepository.save(padrao), LoadCatalogParamsDTO.allTrue());
    }

    public ResPadraoDTO updatePadrao(Long id, EditPadraoDTO dto) {
        Padrao padrao = findByIdOrThrow(id);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            padraoRepository.findByName(dto.getName())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ResourceAlreadyExistsException("Já existe um padrão com o nome " + dto.getName());
                    });

            padrao.setName(dto.getName());
        }
        if (dto.getIsActive() != null) {
            padrao.setIsActive(dto.getIsActive());
        }

        Padrao updated = padraoRepository.save(padrao);
        return catalogMapper.toResponse(updated, LoadCatalogParamsDTO.allTrue());
    }

    public void deletePadrao(Long id) {
        Padrao padrao = findByIdOrThrow(id);
        padraoRepository.delete(padrao);
    }

    public ResPadraoDTO deactivatePadrao(Long id) {
        Padrao existing = findByIdOrThrow(id);
        existing.setIsActive(false);
        return catalogMapper.toResponse(padraoRepository.save(existing), LoadCatalogParamsDTO.allTrue());
    }
}