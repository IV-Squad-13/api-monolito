package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.MaterialDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMaterialDTO;
import com.squad13.apimonolito.exceptions.InvalidAttributeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.repository.catalog.MaterialRepository;
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
public class MaterialService {

    @PersistenceContext
    private EntityManager em;

    private final MaterialRepository materialRepository;

    private final CatalogMapper catalogMapper;

    private final CatalogSearch catalogSearch;

    public List<ResMaterialDTO> findAll(LoadCatalogParamsDTO loadDTO) {
        return materialRepository.findAll()
                .stream()
                .map(material -> catalogMapper.toResponse(material, loadDTO))
                .toList();
    }

    private Material findByIdOrThrow(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material com ID: " + id + " não encontrado."));
    }

    public ResMaterialDTO findById(Long id, LoadCatalogParamsDTO loadDTO) {
        return materialRepository.findById(id)
                .map(material -> catalogMapper.toResponse(material, loadDTO))
                .orElseThrow(() -> new ResourceNotFoundException("Material com ID: " + id + " não encontrado."));
    }

    public List<ResMaterialDTO> findByAttribute(String attribute, String value, LoadCatalogParamsDTO loadDTO) {
        boolean attributeExists = Arrays.stream(Material.class.getDeclaredFields())
                .anyMatch(f -> f.getName().equals(attribute));

        if (!attributeExists)
            throw new InvalidAttributeException("Atributo inválido: " + attribute);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Material> cq = cb.createQuery(Material.class);
        Root<Material> root = cq.from(Material.class);

        Predicate pAttributeMatch = cb.like(cb.lower(root.get(attribute)), value.toLowerCase());

        cq.select(root).where(pAttributeMatch);
        return em.createQuery(cq).getResultList()
                .stream()
                .map(material -> catalogMapper.toResponse(material, loadDTO))
                .toList();
    }

    public ResMaterialDTO createMaterial(MaterialDTO dto) {
        materialRepository.findByName(dto.getName())
                .ifPresent(material -> {
                    throw new ResourceAlreadyExistsException(
                            "Já existe um material com o nome " + dto.getName()
                    );
                });

        Material material = new Material();
        material.setName(dto.getName());
        material.setIsActive(dto.getIsActive());

        return catalogMapper.toResponse(materialRepository.save(material), LoadCatalogParamsDTO.allTrue());
    }

    public ResMaterialDTO updateMaterial(Long id, EditMaterialDTO dto) {
        Material material = findByIdOrThrow(id);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            materialRepository.findByName(dto.getName())
                    .filter(existing -> !existing.getId().equals(dto.getId()))
                    .ifPresent(existing -> {
                        throw new ResourceAlreadyExistsException(
                                "Já existe um material com o nome " + dto.getName()
                        );
                    });

            material.setName(dto.getName());
        }

        if (dto.getIsActive() != null) {
            material.setIsActive(dto.getIsActive());
        }

        return catalogMapper.toResponse(materialRepository.save(material), LoadCatalogParamsDTO.allTrue());
    }

    public void deleteMaterial(Long id) {
        Material material = findByIdOrThrow(id);
        materialRepository.delete(material);
    }

    public ResMaterialDTO deactivateMaterial(Long id) {
        Material existing = findByIdOrThrow(id);
        existing.setIsActive(false);
        return catalogMapper.toResponse(materialRepository.save(existing), LoadCatalogParamsDTO.allTrue());
    }

    public List<ResMaterialDTO> findByFilters(Map<String, String> stringFilters, LoadCatalogParamsDTO loadDTO) {

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

        List<Material> materiais = catalogSearch.findByCriteria(typedFilters, Material.class);

        return materiais.stream()
                .map(material -> catalogMapper.toResponse(material, loadDTO))
                .toList();
    }
}
