package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.AmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResAmbienteDTO;
import com.squad13.apimonolito.exceptions.InvalidAttributeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.repository.catalog.AmbienteRepository;
import com.squad13.apimonolito.util.Mapper;
import com.squad13.apimonolito.util.enums.LocalEnum;
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
public class AmbienteService {

    @PersistenceContext
    private EntityManager em;

    private final AmbienteRepository ambienteRepository;

    private final Mapper mapper;


    public List<ResAmbienteDTO> findAll(Boolean loadAssociations) {
        return ambienteRepository.findAll()
                .stream()
                .map(ambiente -> mapper.toResponse(ambiente, loadAssociations))
                .toList();
    }

    private Ambiente findByIdOrThrow(Long id) {
        return ambienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ambiente com ID: " + id + " não encontrado."));
    }

    public ResAmbienteDTO findById(Long id) {
        return ambienteRepository.findById(id)
                .map(ambiente -> mapper.toResponse(ambiente, true))
                .orElseThrow(() -> new ResourceNotFoundException("Ambiente com ID: " + id + " não encontrado."));
    }

    public List<ResAmbienteDTO> findByAttribute(String attribute, String value) {
        boolean attributeExists = Arrays.stream(Ambiente.class.getDeclaredFields())
                .anyMatch(f -> f.getName().equals(attribute));

        if (!attributeExists)
            throw new InvalidAttributeException("Atributo inválido: " + attribute);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ambiente> cq = cb.createQuery(Ambiente.class);
        Root<Ambiente> root = cq.from(Ambiente.class);

        Predicate pAttributeMatch = cb.like(cb.lower(root.get(attribute)), value.toLowerCase());

        cq.select(root).where(pAttributeMatch);
        return em.createQuery(cq).getResultList()
                .stream()
                .map(ambiente -> mapper.toResponse(ambiente, false))
                .toList();
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

        return mapper.toResponse(ambienteRepository.save(ambiente),  true);
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

        return mapper.toResponse(ambienteRepository.save(ambiente), true);
    }

    public void deleteAmbiente(Long id) {
        Ambiente ambiente = findByIdOrThrow(id);
        ambienteRepository.delete(ambiente);
    }

    public ResAmbienteDTO deactivateAmbiente(Long id) {
        Ambiente existing = findByIdOrThrow(id);
        existing.setIsActive(false);
        return mapper.toResponse(ambienteRepository.save(existing), true);
    }

}
