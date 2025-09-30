package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaMaterialRepository extends JpaRepository<MarcaMaterial, Long> {
    List<MarcaMaterial> findByMaterial_Id(Long materialId);

    List<MarcaMaterial> findByMarca_Id(Long marcaId);

    Optional<MarcaMaterial> findByMarcaAndMaterial(Marca marca, Material material);
}