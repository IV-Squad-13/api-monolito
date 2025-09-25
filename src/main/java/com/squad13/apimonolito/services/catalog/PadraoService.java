package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.PadraoDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditPadraoDTO;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.repository.catalog.PadraoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PadraoService {

    private final PadraoRepository padraoRepository;

    public List<Padrao> findAll() {
        return padraoRepository.findAll();
    }

    public Padrao findByIdOrThrow(Long id) {
        return padraoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de item com id " + id + " não encontrado."));
    }

    public Padrao findByNameOrThrow(String name) {
        return padraoRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de item com nome " + name + " não encontrado."));
    }

    public PadraoDTO createPadrao(PadraoDTO dto) {
        padraoRepository.findByName(dto.getName())
                .ifPresent(a -> {
                    throw new ResourceAlreadyExistsException(
                            "Já existe um padrão com o nome " + dto.getName()
                    );
                });

        Padrao padrao = new Padrao();
        padrao.setName(dto.getName());
        padrao.setIsActive(dto.isActive());

        return mapToDTO(padraoRepository.save(padrao));
    }

    public PadraoDTO updatePadrao(Long id, EditPadraoDTO dto) {
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
        return mapToDTO(updated);
    }

    public void deletePadrao(Long id) {
        Padrao padrao = findByIdOrThrow(id);
        padraoRepository.delete(padrao);
    }

    public Padrao deactivatePadrao(Long id) {
        Padrao existing = findByIdOrThrow(id);
        existing.setIsActive(false);
        return padraoRepository.save(existing);
    }

    private PadraoDTO mapToDTO(Padrao padrao) {
        PadraoDTO dto = new PadraoDTO();
        dto.setName(padrao.getName());
        dto.setActive(padrao.getIsActive());

        return dto;
    }
}