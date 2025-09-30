package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.edit.EditMarcaDTO;
import com.squad13.apimonolito.DTO.catalog.MarcaDTO;
import com.squad13.apimonolito.exceptions.InvalidAttributeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.repository.catalog.MarcaRepository;
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
public class MarcaService {

    @Autowired
    private MarcaRepository marcaRepository;

    @PersistenceContext
    private EntityManager em;

    public List<Marca> findAll(){
        return marcaRepository.findAll();
    }

    public Optional<Marca> findById(Long id){
        return marcaRepository.findById(id);
    }

    public List<Marca> findByAttribute(String attribute, String value) {
        boolean attributeExists = Arrays.stream(Marca.class.getDeclaredFields())
                .anyMatch(f -> f.getName().equals(attribute));

        if (!attributeExists)
            throw new InvalidAttributeException("Atributo inválido: " + attribute);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Marca> cq = cb.createQuery(Marca.class);
        Root<Marca> root = cq.from(Marca.class);

        Predicate pAttributeMatch = cb.like(cb.lower(root.get(attribute)), value.toLowerCase());

        cq.select(root).where(pAttributeMatch);

        return em.createQuery(cq).getResultList();
    }

    public MarcaDTO createMarca(MarcaDTO dto) {
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
        return mapToDTO(saved);
    }

    public MarcaDTO updateMarca(EditMarcaDTO dto) {
        Marca marca = marcaRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada para o ID: " + dto.getId()));

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
        return mapToDTO(updated);
    }

    public void deleteMarca(Long id) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada para o ID: " + id));
        marcaRepository.delete(marca);
    }

    private MarcaDTO mapToDTO(Marca marca) {
        MarcaDTO dto = new MarcaDTO();
        dto.setName(marca.getName());
        dto.setIsActive(marca.getIsActive());
        return dto;
    }
}