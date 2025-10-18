package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    @EntityGraph(attributePaths = {"materialSet"})
    List<Marca> findAll();

    Optional<Marca> findByName(String name);
}