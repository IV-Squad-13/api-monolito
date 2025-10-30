package com.squad13.apimonolito.repository.revision;

import com.squad13.apimonolito.DTO.revision.res.ResRevDTO;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RevisaoRepository extends JpaRepository<Revisao, Long> {
    Optional<Revisao> findByEmpreendimento(Empreendimento empreendimento);

    Optional<Revisao> findByEmpreendimento_Id(Long empreendimentoId);

    void deleteByEmpreendimento_Id(Long empreendimentoId);
}