package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {
    Optional<Catalogo> findByName(String name);
}