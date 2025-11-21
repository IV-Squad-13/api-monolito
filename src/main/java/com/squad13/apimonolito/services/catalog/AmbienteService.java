package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.AmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResAmbienteDTO;
import com.squad13.apimonolito.exceptions.exceptions.InvalidAttributeException;
import com.squad13.apimonolito.exceptions.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.repository.catalog.AmbienteRepository;
import com.squad13.apimonolito.util.enums.LocalEnum;
import com.squad13.apimonolito.util.mapper.CatalogMapper;
import com.squad13.apimonolito.util.search.CatalogSearch;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@RequiredArgsConstructor
public class AmbienteService {
    private final CatalogSearch catalogSearch;

    private final AmbienteRepository ambienteRepository;

    private final CatalogMapper catalogMapper;

    public List<ResAmbienteDTO> findAll(LoadCatalogParamsDTO params) {
        return ambienteRepository.findAll()
                .stream()
                .map(ambiente -> catalogMapper.toResponse(ambiente, params))
                .toList();
    }

    private Ambiente findByIdOrThrow(Long id) {
        return ambienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ambiente com ID: " + id + " não encontrado."));
    }

    public ResAmbienteDTO findById(Long id, LoadCatalogParamsDTO loadParams) {
        return ambienteRepository.findById(id)
                .map(ambiente -> catalogMapper.toResponse(ambiente, loadParams))
                .orElseThrow(() -> new ResourceNotFoundException("Ambiente com ID: " + id + " não encontrado."));
    }

    public ResAmbienteDTO createAmbiente(AmbienteDTO dto) {
        ambienteRepository.findByNameAndLocal(dto.getName(), dto.getLocal())
                .ifPresent(a -> {
                    throw new ResourceAlreadyExistsException(
                            "Já existe um ambiente com o nome " + dto.getName() + " no local " + dto.getLocal()
                    );
                });

        Ambiente ambiente = new Ambiente();
        ambiente.setName(dto.getName());
        ambiente.setLocal(dto.getLocal());
        ambiente.setIsActive(dto.getIsActive());

        return catalogMapper.toResponse(ambienteRepository.save(ambiente),  LoadCatalogParamsDTO.allTrue());
    }

    private void ensureUniqueNameAndLocal(Ambiente ambiente, EditAmbienteDTO dto) {
        String newName = dto.getName() != null && !dto.getName().isBlank()
                ? dto.getName()
                : ambiente.getName();

        LocalEnum newLocal = dto.getLocal() != null
                ? dto.getLocal()
                : ambiente.getLocal();

        ambienteRepository.findByNameAndLocal(newName, newLocal)
                .filter(existing -> !existing.getId().equals(ambiente.getId()))
                .ifPresent(existing -> {
                    throw new ResourceAlreadyExistsException(
                            "Já existe um ambiente com o nome " + newName + " no local " + newLocal
                    );
                });
    }

    public ResAmbienteDTO updateAmbiente(Long id, EditAmbienteDTO dto) {
        Ambiente ambiente = findByIdOrThrow(id);
        ensureUniqueNameAndLocal(ambiente, dto);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            ambiente.setName(dto.getName());
        }

        if (dto.getLocal() != null) {
            ambiente.setLocal(dto.getLocal());
        }

        if (dto.getIsActive() != null) {
            ambiente.setIsActive(dto.getIsActive());
        }

        return catalogMapper.toResponse(ambienteRepository.save(ambiente), LoadCatalogParamsDTO.allTrue());
    }

    public void deleteAmbiente(Long id) {
        Ambiente ambiente = findByIdOrThrow(id);
        ambienteRepository.delete(ambiente);
    }

    public ResAmbienteDTO deactivateAmbiente(Long id) {
        Ambiente existing = findByIdOrThrow(id);
        existing.setIsActive(false);
        return catalogMapper.toResponse(ambienteRepository.save(existing), LoadCatalogParamsDTO.allTrue());
    }

    public List<ResAmbienteDTO> findByFilters(Map<String, String> stringFilters, LoadCatalogParamsDTO loadDTO) {
        Map<String, Object> typedFilters = new HashMap<>();

        for (Map.Entry<String, String> entry : stringFilters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            switch (key) {
                case "name":
                    typedFilters.put(key, value);
                    break;

                case "local":
                    try {
                        typedFilters.put(key, LocalEnum.valueOf(value.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new InvalidAttributeException("Valor inválido para o filtro 'local': " + value);
                    }
                    break;

                case "id":
                    try {
                        typedFilters.put(key, Long.parseLong(value));
                    } catch (NumberFormatException e) {
                        throw new InvalidAttributeException("Valor inválido para o filtro 'id': " + value);
                    }
                    break;

                case "isActive":
                    typedFilters.put(key, Boolean.parseBoolean(value));
                    break;

                default:
                    System.out.println("Ignorando filtro desconhecido: " + key);

            }
        }

        List<Ambiente> ambientes = catalogSearch.findByCriteria(typedFilters, Ambiente.class);

        return ambientes.stream()
                .map(ambiente -> catalogMapper.toResponse(ambiente, loadDTO))
                .toList();
    }
}
