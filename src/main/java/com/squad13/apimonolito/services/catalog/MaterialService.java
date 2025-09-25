package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.EditMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.MaterialDTO;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.repository.catalog.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public List<Material> findAll(){
        return materialRepository.findAll();
    }

    public Optional<Material> findById(Long id){
        return materialRepository.findById(id);
    }

    public MaterialDTO createMaterial(MaterialDTO dto) {
        Material material = new Material();
        material.setName(dto.getName());
        material.setIsActive(dto.getIsActive());

        Material saved = materialRepository.save(material);
        return mapToDTO(saved);
    }

    public MaterialDTO updateMaterial(EditMaterialDTO dto) {
        Material material = materialRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado."));
        if (dto.getName() != null && !dto.getName().isBlank()) {
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
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado."));
        if (material.getMarcaSet() != null && !material.getMarcaSet().isEmpty()) {
            throw new IllegalStateException("Não é possível excluir o material pois há marcas vinculadas.");
        }
        materialRepository.delete(material);
    }

    private MaterialDTO mapToDTO(Material material) {
        MaterialDTO dto = new MaterialDTO();
        dto.setName(material.getName());
        dto.setIsActive(material.getIsActive());
        return dto;
    }
}
