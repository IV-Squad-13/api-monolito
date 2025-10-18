package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.models.catalog.associative.ComposicaoAmbiente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PadraoRepository extends JpaRepository<Padrao, Long> {
    @EntityGraph(attributePaths = {"materialSet", "ambienteSet", "empreendimentoSet"})
    List<Padrao> findAll();

    Optional<Padrao> findByName(String name);
}