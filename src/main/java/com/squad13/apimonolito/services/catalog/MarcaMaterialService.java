package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.LoadParametersDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMarcaDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMarcaMaterialDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResMaterialDTO;
import com.squad13.apimonolito.exceptions.AssociationAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import com.squad13.apimonolito.repository.catalog.*;
import com.squad13.apimonolito.util.Mapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class MarcaMaterialService {

    private final MarcaMaterialRepository marcaMaterialRepository;
    private final MaterialRepository materialRepository;
    private final MarcaRepository marcaRepository;

    private final Mapper mapper;

    public List<ResMarcaDTO> findMaterialMarcas(Long materialId) {
        return marcaMaterialRepository.findByMaterial_Id(materialId)
                .stream()
                .map(MarcaMaterial::getMarca)
                .map(marca -> mapper.toResponse(marca, LoadParametersDTO.allFalse()))
                .toList();
    }

    public List<ResMaterialDTO> findMarcaMaterials(Long marcaId) {
        return marcaMaterialRepository.findByMarca_Id(marcaId)
                .stream()
                .map(MarcaMaterial::getMaterial)
                .map(material -> mapper.toResponse(material, LoadParametersDTO.allFalse()))
                .toList();
    }

    private Marca findMarcaByIdOrThrow(Long id) {
        return marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nenhuma marca com ID: " + id + " foi encontrada"));
    }

    private Material findMaterialByIdOrThrow(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nenhum material com ID: " + id + " foi encontrado"));
    }

    public ResMarcaMaterialDTO associateMarcaAndMaterial(Long marcaId, Long materialId) {
        Marca marca = findMarcaByIdOrThrow(marcaId);
        Material material = findMaterialByIdOrThrow(materialId);

        marcaMaterialRepository.findByMarcaAndMaterial(marca, material)
                .ifPresent(rel -> {
                    throw new AssociationAlreadyExistsException(
                            "Associação já existente entre a marca " + marcaId + " e o material " + materialId);
                });

        MarcaMaterial marcaMaterial = new MarcaMaterial();
        marcaMaterial.setMarca(marca);
        marcaMaterial.setMaterial(material);

        material.getMarcaSet().add(marcaMaterial);
        materialRepository.save(material);

        return mapper.toResponse(marcaMaterial);
    }

    public void deleteMarcaAndMaterialAssociation(Long marcaId, Long materialId) {
        Marca marca = findMarcaByIdOrThrow(marcaId);
        Material material = findMaterialByIdOrThrow(materialId);

        MarcaMaterial marcaMaterial = marcaMaterialRepository.findByMarcaAndMaterial(marca, material)
                .orElseThrow(() -> new ResourceNotFoundException("Associação entre a marca " + marcaId + " e o material " + materialId + " não encontrada"));
        marcaMaterialRepository.delete(marcaMaterial);
    }
}