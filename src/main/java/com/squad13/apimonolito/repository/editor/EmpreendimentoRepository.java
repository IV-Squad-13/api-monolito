package com.squad13.apimonolito.repository.editor;

import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpreendimentoRepository extends JpaRepository<Empreendimento, Long> {  }