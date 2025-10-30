package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.MarcaDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditMarcaDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMarcaDTO;
import com.squad13.apimonolito.exceptions.InvalidAttributeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.repository.catalog.MarcaRepository;
import com.squad13.apimonolito.util.mapper.CatalogMapper;
import com.squad13.apimonolito.util.mappers.CatalogMapper;
import com.squad13.apimonolito.util.search.CatalogSearch;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;

    private final CatalogMapper catalogMapper;

    private final CatalogSearch catalogSearch;

    @PersistenceContext
    private EntityManager em;

    public List<ResMarcaDTO> findAll(LoadCatalogParamsDTO loadDTO) {
        return marcaRepository.findAll()
                .stream()
                .map(marca -> catalogMapper.toResponse(marca, loadDTO))
                .toList();
    }

    private Marca findByIdOrThrow(Long id) {
        return marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca com ID: " + id + " não encontrado."));
    }

    public ResMarcaDTO findById(Long id, LoadCatalogParamsDTO loadDTO) {
        return marcaRepository.findById(id)
                .map(marca -> catalogMapper.toResponse(marca, loadDTO))
                .orElseThrow(() -> new ResourceNotFoundException("Marca com ID: " + id + " não encontrado."));
    }

    public List<ResMarcaDTO> findByAttribute(String attribute, String value, LoadCatalogParamsDTO loadDTO) {
        boolean attributeExists = Arrays.stream(Marca.class.getDeclaredFields())
                .anyMatch(f -> f.getName().equals(attribute));

        if (!attributeExists)
            throw new InvalidAttributeException("Atributo inválido: " + attribute);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Marca> cq = cb.createQuery(Marca.class);
        Root<Marca> root = cq.from(Marca.class);

        Predicate pAttributeMatch = cb.like(cb.lower(root.get(attribute)), value.toLowerCase());

        cq.select(root).where(pAttributeMatch);

        return em.createQuery(cq).getResultList()
                .stream()
                .map(m -> catalogMapper.toResponse(m, loadDTO))
                .toList();
    }

    public ResMarcaDTO createMarca(MarcaDTO dto) {
        marcaRepository.findByName(dto.getName())
                .ifPresent(m -> {
                    throw new ResourceAlreadyExistsException(
                            "Já existe uma marca com o nome " + dto.getName()
                    );
                });

        Marca marca = new Marca();
        marca.setName(dto.getName());
        marca.setIsActive(dto.getIsActive());

        Marca saved = marcaRepository.save(marca);
        return catalogMapper.toResponse(saved, LoadCatalogParamsDTO.allTrue());
    }

    public ResMarcaDTO updateMarca(Long id, EditMarcaDTO dto) {
        Marca marca = findByIdOrThrow(id);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            marcaRepository.findByName(dto.getName())
                    .filter(existing -> !existing.getId().equals(dto.getId()))
                    .ifPresent(existing -> {
                        throw new ResourceAlreadyExistsException(
                                "Já existe uma marca com o nome " + dto.getName()
                        );
                    });

            marca.setName(dto.getName());
        }
        if (dto.getIsActive() != null) {
            marca.setIsActive(dto.getIsActive());
        }

        Marca updated = marcaRepository.save(marca);
        return catalogMapper.toResponse(updated, LoadCatalogParamsDTO.allTrue());
    }

    public void deleteMarca(Long id) {
        Marca marca = findByIdOrThrow(id);
        marcaRepository.delete(marca);
    }

    public ResMarcaDTO deactivateMarca(Long id) {
        Marca existing = findByIdOrThrow(id);
        existing.setIsActive(false);
        return catalogMapper.toResponse(marcaRepository.save(existing), LoadCatalogParamsDTO.allTrue());
    }

    public List<ResMarcaDTO> findByFilters(Map<String, String> stringFilters, LoadCatalogParamsDTO loadDTO) {

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

        List<Marca> marcas = catalogSearch.findByCriteria(typedFilters, Marca.class);

        return marcas.stream()
                .map(marca -> catalogMapper.toResponse(marca, loadDTO))
                .toList();
    }
}