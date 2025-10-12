package com.squad13.apimonolito.repository.revision;

import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RevisaoRepository extends JpaRepository<Revisao, Long> {
    List<Revisao> findByEmpreendimento(Empreendimento empreendimento);
}