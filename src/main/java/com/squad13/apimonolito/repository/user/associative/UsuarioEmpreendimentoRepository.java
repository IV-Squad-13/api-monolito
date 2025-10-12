package com.squad13.apimonolito.repository.user.associative;

import com.squad13.apimonolito.models.user.associative.UsuarioEmpreendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioEmpreendimentoRepository extends JpaRepository<UsuarioEmpreendimento, Long> {
}
