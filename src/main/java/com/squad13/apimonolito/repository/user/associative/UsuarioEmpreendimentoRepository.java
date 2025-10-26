package com.squad13.apimonolito.repository.user.associative;

import com.squad13.apimonolito.models.user.associative.UsuarioEmpreendimento;
import com.squad13.apimonolito.util.enums.AccessEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioEmpreendimentoRepository extends JpaRepository<UsuarioEmpreendimento, Long> {

    Optional<UsuarioEmpreendimento> findByUsuario_IdAndEmpreendimento_Id(Long usuarioId, Long empreendimentoId);

    void deleteByEmpreendimento_Id(Long empreendimentoId);

    Optional<UsuarioEmpreendimento> findByUsuario_IdAndEmpreendimento_IdAndAccessLevel(Long usuarioId, Long empreendimentoId, AccessEnum accessLevel);
}
