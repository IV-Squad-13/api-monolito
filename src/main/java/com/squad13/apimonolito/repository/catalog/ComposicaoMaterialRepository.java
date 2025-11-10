package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.associative.ComposicaoAmbiente;
import com.squad13.apimonolito.models.catalog.associative.ComposicaoMaterial;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComposicaoMaterialRepository extends JpaRepository<ComposicaoMaterial, Long> {
    @EntityGraph(attributePaths = {"padrao", "compositor"})
    List<ComposicaoMaterial> findAll();

    List<ComposicaoMaterial> findByCompositor_Material_Id(Long ambienteId);

    @EntityGraph(attributePaths = {
            "padrao",
            "compositor",
            "compositor.material",
            "compositor.marca"
    })
    List<ComposicaoMaterial> findByPadrao_Id(Long padraoId);

    @EntityGraph(attributePaths = {
            "padrao",
            "compositor",
            "compositor.material",
            "compositor.marca"
    })
    @Query("""
        SELECT c FROM ComposicaoMaterial c
        WHERE c.padrao.id = :padraoId
          AND c.compositor.material.id = :materialId
          AND c.compositor.marca.id = :marcaId
    """)
    List<ComposicaoMaterial> findFiltered(
            @Param("padraoId") Long padraoId,
            @Param("materialId") Long materialId,
            @Param("marcaId") Long marcaId
    );

    @EntityGraph(attributePaths = {
            "padrao",
            "compositor",
            "compositor.material",
            "compositor.marca"
    })
    @Query("""
        SELECT c FROM ComposicaoMaterial c
        WHERE c.padrao.id = :padraoId
          AND c.compositor.material.id = :materialId
    """)
    List<ComposicaoMaterial> findFilteredByMaterial(
            @Param("padraoId") Long padraoId,
            @Param("materialId") Long materialId
    );

    @EntityGraph(attributePaths = {
            "padrao",
            "compositor",
            "compositor.material",
            "compositor.marca"
    })
    @Query("""
        SELECT c FROM ComposicaoMaterial c
        WHERE c.padrao.id = :padraoId
          AND c.compositor.marca.id = :marcaId
    """)
    List<ComposicaoMaterial> findFilteredByMarca(
            @Param("padraoId") Long padraoId,
            @Param("marcaId") Long marcaId
    );

    boolean deleteByPadrao_IdAndCompositor_Material_Id(Long padraoId, Long materialId);
}