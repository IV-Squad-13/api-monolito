package com.squad13.apimonolito.repository.user;

import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.util.enums.PapelEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNome(String nome);

    boolean existsByNome(String nome);

    boolean existsByEmail(String email);

    List<Usuario> findByPapel_Nome(PapelEnum nome);

    Optional<Usuario> findByEmail(String email);
}
