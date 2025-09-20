package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.AmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.EditAmbienteDTO;
import com.squad13.apimonolito.exceptions.InvalidAttributeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.repository.catalog.AmbienteRepository;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AmbienteService {

    @Autowired
    private AmbienteRepository ambienteRepository;

    @PersistenceContext
    private EntityManager em;

    public List<Ambiente> findAll() {
        return ambienteRepository.findAll();
    }

    public Optional<Ambiente> findById(Long id) {
        return ambienteRepository.findById(id);
    }

    public List<Ambiente> findByAttribute(String attribute, String value) {
        boolean attributeExists = Arrays.stream(Ambiente.class.getDeclaredFields())
                .anyMatch(f -> f.getName().equals(attribute));

        if (!attributeExists)
            throw new InvalidAttributeException("Atributo inválido: " + attribute);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ambiente> cq = cb.createQuery(Ambiente.class);
        Root<Ambiente> root = cq.from(Ambiente.class);

        Predicate pAttributeMatch = cb.like(cb.lower(root.get(attribute)), value.toLowerCase());

        cq.select(root).where(pAttributeMatch);
        return em.createQuery(cq).getResultList();
    }

    public AmbienteDTO createAmbiente(AmbienteDTO dto) {
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

        return mapToDTO(ambienteRepository.save(ambiente));
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

    public AmbienteDTO updateAmbiente(EditAmbienteDTO dto) {
        Ambiente ambiente = ambienteRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ambiente não encontrado para o ID: " + dto.getId()));

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

        return mapToDTO(ambienteRepository.save(ambiente));
    }

    public void deleteAmbiente(Long id) {
        Ambiente ambiente = ambienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ambiente não encontrado para o ID: " + id));

        ambienteRepository.delete(ambiente);
    }

    private AmbienteDTO mapToDTO(Ambiente ambiente) {
        AmbienteDTO dto = new AmbienteDTO();
        dto.setName(ambiente.getName());
        dto.setLocal(ambiente.getLocal());
        dto.setIsActive(ambiente.getIsActive());
        return dto;
    }
}
