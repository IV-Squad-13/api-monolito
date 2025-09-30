package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.AmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.EditAmbienteDTO;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.repository.catalog.AmbienteRepository;
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

    public AmbienteDTO updateAmbiente(EditAmbienteDTO dto) {
        Ambiente ambiente = ambienteRepository.findById(dto.getTypeId()).orElseThrow(()-> new IllegalArgumentException("Ambiente não encontrado"));

        if(dto.getName() != null && !dto.getName().isBlank()) {
            ambiente.setName(dto.getName());
        }

        if (dto.getLocal() != null) {
            ambiente.setLocal(dto.getLocal());
        }
        if (dto.getIsActive() != null) {
            ambiente.setIsActive(dto.getIsActive());
        }

        Ambiente updated = ambienteRepository.save(ambiente);
        return mapToDTO(updated);
    }

    public void deleteAmbiente(Long id) {
        Ambiente ambiente = ambienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ambiente não encontrado."));
        if (ambiente.getItemAmbiente() != null && !ambiente.getItemAmbiente().isEmpty()) {
            throw new IllegalStateException("Não é possível excluir o ambiente pois há itens vinculados.");
        }
        ambienteRepository.delete(ambiente);
    }

    private AmbienteDTO mapToDTO(Ambiente ambiente) {
        AmbienteDTO dto = new AmbienteDTO();
        dto.setName(ambiente.getName());
        dto.setIsActive(ambiente.getIsActive());
        return dto;
    }

}
