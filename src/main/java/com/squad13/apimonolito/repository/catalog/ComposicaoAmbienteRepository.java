package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.associative.ComposicaoAmbiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComposicaoAmbienteRepository extends JpaRepository<ComposicaoAmbiente, Long> {
    List<ComposicaoAmbiente> findByCompositor_Ambiente_Id(Long ambienteId);

    List<ComposicaoAmbiente> findByPadrao_Id(Long padraoId);

    boolean deleteByPadrao_IdAndCompositor_Ambiente_Id(Long padraoId, Long ambienteId);
}