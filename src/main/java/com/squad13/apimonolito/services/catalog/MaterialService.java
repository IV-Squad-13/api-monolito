package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.EditMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.MaterialDTO;
import com.squad13.apimonolito.exceptions.InvalidAttributeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.repository.catalog.MaterialRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    @PersistenceContext
    private EntityManager em;

    public List<Material> findAll(){
        return materialRepository.findAll();
    }

    public Optional<Material> findById(Long id){
        return materialRepository.findById(id);
    }

    public List<Material> findByAttribute(String attribute, String value) {
        boolean attributeExists = Arrays.stream(Material.class.getDeclaredFields())
                .anyMatch(f -> f.getName().equals(attribute));

        if (!attributeExists)
            throw new InvalidAttributeException("Atributo inválido: " + attribute);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Material> cq = cb.createQuery(Material.class);
        Root<Material> root = cq.from(Material.class);

        Predicate pAttributeMatch = cb.like(cb.lower(root.get(attribute)), value.toLowerCase());

        cq.select(root).where(pAttributeMatch);
        return em.createQuery(cq).getResultList();
    }

    public MaterialDTO createMaterial(MaterialDTO dto) {
        materialRepository.findByName(dto.getName())
                .ifPresent(material -> {
                    throw new ResourceAlreadyExistsException(
                            "Já existe um material com o nome " + dto.getName()
                    );
                });

        Material material = new Material();
        material.setName(dto.getName());
        material.setIsActive(dto.getIsActive());

        Material saved = materialRepository.save(material);
        return mapToDTO(saved);
    }

    public MaterialDTO updateMaterial(EditMaterialDTO dto) {
        Material material = materialRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado para o ID " + dto.getId()));

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

        Material updated = materialRepository.save(material);
        return mapToDTO(updated);
    }

    public void deleteMaterial(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado para o ID " + id));
        materialRepository.delete(material);
    }

    private MaterialDTO mapToDTO(Material material) {
        MaterialDTO dto = new MaterialDTO();
        dto.setName(material.getName());
        dto.setIsActive(material.getIsActive());
        return dto;
    }
}
