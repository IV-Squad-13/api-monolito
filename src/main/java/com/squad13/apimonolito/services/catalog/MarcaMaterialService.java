package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.exceptions.AssociationAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import com.squad13.apimonolito.repository.catalog.*;
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

    public List<Marca> findMaterialMarcas(Long materialId) {
        return marcaMaterialRepository.findByMaterial_Id(materialId)
                .stream()
                .map(MarcaMaterial::getMarca)
                .toList();
    }

    public List<Material> findMarcaMaterials(Long marcaId) {
        return marcaMaterialRepository.findByMarca_Id(marcaId)
                .stream()
                .map(MarcaMaterial::getMaterial)
                .toList();
    }

    private Marca findMarcaById(Long id) {
        return marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nenhuma marca com ID: " + id + " foi encontrada"));
    }

    private Material findMaterialById(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nenhum material com ID: " + id + " foi encontrado"));
    }

    public MarcaMaterial associateMarcaAndMaterial(Long marcaId, Long materialId) {
        Marca marca = findMarcaById(marcaId);
        Material material = findMaterialById(materialId);

        marcaMaterialRepository.findByMarcaAndMaterial(marca, material)
                .ifPresent(rel -> {
                    throw new AssociationAlreadyExistsException(
                            "Associação já existente entre a marca " + marcaId + " e o material " + materialId);
                });

        MarcaMaterial marcaMaterial = new MarcaMaterial();
        marcaMaterial.setMarca(marca);
        marcaMaterial.setMaterial(material);

        // Add to parent collection and save
        material.getMarcaSet().add(marcaMaterial);
        materialRepository.save(material);

        return marcaMaterial;
    }

    public void deleteMarcaAndMaterialAssociation(Long marcaId, Long materialId) {
        Marca marca = findMarcaById(marcaId);
        Material material = findMaterialById(materialId);

        MarcaMaterial marcaMaterial = marcaMaterialRepository.findByMarcaAndMaterial(marca, material)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Associação entre a marca " + marcaId + " e o material " + materialId + " não encontrada"));

        material.getMarcaSet().remove(marcaMaterial);
        materialRepository.save(material);
    }
}