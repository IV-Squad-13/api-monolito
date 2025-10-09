package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.LoadParametersDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditMarcaDTO;
import com.squad13.apimonolito.DTO.catalog.MarcaDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMarcaDTO;
import com.squad13.apimonolito.exceptions.InvalidAttributeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.repository.catalog.MarcaRepository;
import com.squad13.apimonolito.util.Mapper;
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
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;

    private final Mapper mapper;

    @PersistenceContext
    private EntityManager em;

    public List<ResMarcaDTO> findAll(LoadParametersDTO loadDTO) {
        return marcaRepository.findAll()
                .stream()
                .map(marca -> mapper.toResponse(marca, loadDTO))
                .toList();
    }

    private Marca findByIdOrThrow(Long id) {
        return marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca com ID: " + id + " não encontrado."));
    }

    public ResMarcaDTO findById(Long id, LoadParametersDTO loadDTO) {
        return marcaRepository.findById(id)
                .map(marca -> mapper.toResponse(marca, loadDTO))
                .orElseThrow(() -> new ResourceNotFoundException("Marca com ID: " + id + " não encontrado."));
    }

    public List<ResMarcaDTO> findByAttribute(String attribute, String value, LoadParametersDTO loadDTO) {
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
                .map(m -> mapper.toResponse(m, loadDTO))
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
        return mapper.toResponse(saved, LoadParametersDTO.allTrue());
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
        return mapper.toResponse(updated, LoadParametersDTO.allTrue());
    }

    public void deleteMarca(Long id) {
        Marca marca = findByIdOrThrow(id);
        marcaRepository.delete(marca);
    }

    public ResMarcaDTO deactivateMarca(Long id) {
        Marca existing = findByIdOrThrow(id);
        existing.setIsActive(false);
        return mapper.toResponse(marcaRepository.save(existing), LoadParametersDTO.allTrue());
    }
}