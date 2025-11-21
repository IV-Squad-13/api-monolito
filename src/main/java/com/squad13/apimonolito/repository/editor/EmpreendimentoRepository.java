package com.squad13.apimonolito.repository.editor;

import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpreendimentoRepository extends JpaRepository<Empreendimento, Long> {
    @EntityGraph(attributePaths = {
            "padrao", "usuarioSet", "processes", "processes.revision"
    })
    Optional<Empreendimento> findByName(String name);

    @EntityGraph(attributePaths = {
            "padrao", "usuarioSet", "processes", "processes.revision"
    })
    List<Empreendimento> findAll();

    @EntityGraph(attributePaths = {
            "padrao", "usuarioSet", "processes", "processes.revision"
    })
    Optional<Empreendimento> findById(Long id);
}