package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Padrao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PadraoRepository extends JpaRepository<Padrao, Long> {
    @EntityGraph(attributePaths = {
            "materialSet", "ambienteSet", "empreendimentoSet",
            "ambienteSet.compositor", "ambienteSet.compositor.ambiente", "ambienteSet.compositor.itemDesc",
            "materialSet.compositor", "materialSet.compositor.material", "materialSet.compositor.marca"
    })
    List<Padrao> findAll();

    @EntityGraph(attributePaths = {
            "materialSet", "ambienteSet", "empreendimentoSet",
            "ambienteSet.compositor", "ambienteSet.compositor.ambiente", "ambienteSet.compositor.itemDesc",
            "materialSet.compositor", "materialSet.compositor.material", "materialSet.compositor.marca"
    })
    Optional<Padrao> findById(Long id);

    Optional<Padrao> findByName(String name);
}