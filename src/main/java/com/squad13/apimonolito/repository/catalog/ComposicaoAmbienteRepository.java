package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.associative.ComposicaoAmbiente;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComposicaoAmbienteRepository extends JpaRepository<ComposicaoAmbiente, Long> {
    @EntityGraph(attributePaths = {"padrao", "compositor"})
    List<ComposicaoAmbiente> findAll();

    List<ComposicaoAmbiente> findByCompositor_Ambiente_Id(Long ambienteId);

    @EntityGraph(attributePaths = {"padrao", "compositor", "compositor.itemDesc", "compositor.itemDesc.type", "compositor.ambiente"})
    List<ComposicaoAmbiente> findByPadrao_Id(Long padraoId);

    boolean deleteByPadrao_IdAndCompositor_Ambiente_Id(Long padraoId, Long ambienteId);
}