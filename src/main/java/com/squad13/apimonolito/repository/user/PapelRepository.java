package com.squad13.apimonolito.repository.user;

import com.squad13.apimonolito.models.user.Papel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PapelRepository extends JpaRepository<Papel,Long> {
    Optional<Papel> findByNome(String nome);
}
