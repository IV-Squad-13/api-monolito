package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.EditPadraoDTO;
import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.PadraoDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMarcaMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResPadraoDTO;
import com.squad13.apimonolito.exceptions.InvalidAttributeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.repository.catalog.PadraoRepository;
import com.squad13.apimonolito.util.search.CatalogSearch;
import com.squad13.apimonolito.util.mapper.CatalogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PadraoService {

    private final PadraoRepository padraoRepository;

    private final CatalogMapper catalogMapper;
    private final ComposicaoService composicaoService;

    private final CatalogSearch catalogSearch;

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

    public List<ResPadraoDTO> findByFilters(Map<String, String> stringFilters, LoadCatalogParamsDTO loadDTO) {

        Map<String, Object> typedFilters = new HashMap<>();

        for (Map.Entry<String, String> entry : stringFilters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            switch (key) {
                case "name":
                    typedFilters.put(key, value);
                    break;

                case "isActive":
                    typedFilters.put(key, Boolean.parseBoolean(value));
                    break;

                case "id":
                    try {
                        typedFilters.put(key, Long.parseLong(value));
                    } catch (NumberFormatException e) {
                        throw new InvalidAttributeException("Valor inválido para o filtro 'id': " + value);
                    }
                    break;

                default:
                    System.out.println("Ignorando filtro desconhecido: " + key);
            }
        }

        List<Padrao> padroes = catalogSearch.findByCriteria(typedFilters, Padrao.class);

        return padroes.stream()
                .map(padrao -> catalogMapper.toResponse(padrao, loadDTO))
                .toList();
    }
}