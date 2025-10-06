package com.squad13.apimonolito.repository.user;

import com.squad13.apimonolito.models.user.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByNome(String nome);
    boolean existsByNome(String nome);
    boolean existsByEmail(String email);
}
