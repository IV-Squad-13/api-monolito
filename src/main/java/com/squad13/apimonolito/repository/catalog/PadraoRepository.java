package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Padrao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PadraoRepository extends JpaRepository<Padrao, Long> {
    Optional<Padrao> findByName(String name);
}