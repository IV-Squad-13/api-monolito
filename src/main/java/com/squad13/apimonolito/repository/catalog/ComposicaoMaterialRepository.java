package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.associative.ComposicaoMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComposicaoMaterialRepository extends JpaRepository<ComposicaoMaterial, Long> {
    List<ComposicaoMaterial> findByCompositor_Material_Id(Long ambienteId);

    List<ComposicaoMaterial> findByPadrao_Id(Long padraoId);

    boolean deleteByPadrao_IdAndCompositor_Material_Id(Long padraoId, Long materialId);
}