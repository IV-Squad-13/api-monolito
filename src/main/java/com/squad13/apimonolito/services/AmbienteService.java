package com.squad13.apimonolito.services;

import com.squad13.apimonolito.DTO.AmbienteDTO;
import com.squad13.apimonolito.models.Ambiente;
import com.squad13.apimonolito.repository.AmbienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AmbienteService {

    @Autowired
    private AmbienteRepository ambienteRepository;

    public List<Ambiente> findAll() {
        return ambienteRepository.findAll();
    }

    public Optional<Ambiente> findById(Long id) {
        return ambienteRepository.findById(id);
    }

    public AmbienteDTO createAmbiente(AmbienteDTO dto) {
        Ambiente ambiente = new Ambiente();
        ambiente.setName(dto.getName());
        ambiente.setIsActive(dto.getIsActive());

        Ambiente saved = ambienteRepository.save(ambiente);

        return mapToDTO(saved);
    }

    private AmbienteDTO mapToDTO(Ambiente ambiente) {
        AmbienteDTO dto = new AmbienteDTO();
        dto.setName(ambiente.getName());
        dto.setIsActive(ambiente.getIsActive());
        return dto;
    }

}
