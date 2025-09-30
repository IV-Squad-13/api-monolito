package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.EditMarcaDTO;
import com.squad13.apimonolito.DTO.catalog.MarcaDTO;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.repository.catalog.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarcaService {

    @Autowired
    private MarcaRepository marcaRepository;

    public List<Marca> findAll(){
        return marcaRepository.findAll();
    }

    public Optional<Marca> findById(Long id){
        return marcaRepository.findById(id);
    }

    public MarcaDTO createMarca(MarcaDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("O nome da marca não pode ser vazio.");
        }
        Marca marca = new Marca();
        marca.setName(dto.getName());
        marca.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        Marca saved = marcaRepository.save(marca);
        return mapToDTO(saved);
    }

    private MarcaDTO mapToDTO(Marca marca) {
        MarcaDTO dto = new MarcaDTO();
        dto.setName(marca.getName());
        dto.setIsActive(marca.getIsActive());
        return dto;
    }

    public MarcaDTO updateMarca(EditMarcaDTO dto) {
        Marca marca = marcaRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Marca não encontrada."));

        if (dto.getName() != null && !dto.getName().isBlank()) {
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
                .orElseThrow(() -> new IllegalArgumentException("Marca não encontrada."));
        if (marca.getMaterialSet() != null && !marca.getMaterialSet().isEmpty()) {
            throw new IllegalStateException("Não é possível excluir a marca pois há materiais vinculados.");
        }
        marcaRepository.delete(marca);
    }

}
