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
public interface ComposicaoAmbienteRepository extends JpaRepository<ComposicaoAmbiente, Long> {
    @EntityGraph(attributePaths = {"padrao", "compositor"})
    List<ComposicaoAmbiente> findAll();

    List<ComposicaoAmbiente> findByCompositor_Ambiente_Id(Long ambienteId);

    @EntityGraph(attributePaths = {
            "padrao",
            "compositor",
            "compositor.itemDesc",
            "compositor.itemDesc.type",
            "compositor.ambiente"
    })
    List<ComposicaoAmbiente> findByPadrao_Id(Long padraoId);

    @EntityGraph(attributePaths = {
            "padrao",
            "compositor",
            "compositor.itemDesc",
            "compositor.itemDesc.type",
            "compositor.ambiente"
    })
    @Query("""
        SELECT c FROM ComposicaoAmbiente c
        WHERE c.padrao.id = :padraoId
          AND (:ambienteId IS NULL OR c.compositor.ambiente.id = :ambienteId)
          AND (:itemId IS NULL OR c.compositor.itemDesc.id = :itemId)
    """)
    List<ComposicaoAmbiente> findFiltered(
            @Param("padraoId") Long padraoId,
            @Param("ambienteId") Long ambienteId,
            @Param("itemId") Long itemId
    );

    @EntityGraph(attributePaths = {
            "padrao",
            "compositor",
            "compositor.itemDesc",
            "compositor.itemDesc.type",
            "compositor.ambiente"
    })
    @Query("""
        SELECT c FROM ComposicaoAmbiente c
        WHERE c.padrao.id = :padraoId
          AND c.compositor.ambiente.id = :ambienteId
    """)
    List<ComposicaoAmbiente> findFilteredByAmbiente(
            @Param("padraoId") Long padraoId,
            @Param("ambienteId") Long ambienteId
    );

    @EntityGraph(attributePaths = {
            "padrao",
            "compositor",
            "compositor.itemDesc",
            "compositor.itemDesc.type",
            "compositor.ambiente"
    })
    @Query("""
        SELECT c FROM ComposicaoAmbiente c
        WHERE c.padrao.id = :padraoId
          AND c.compositor.itemDesc.id = :itemId
    """)
    List<ComposicaoAmbiente> findFilteredByItem(
            @Param("padraoId") Long padraoId,
            @Param("itemId") Long itemId
    );

    boolean deleteByPadrao_IdAndCompositor_Ambiente_Id(Long padraoId, Long ambienteId);
}