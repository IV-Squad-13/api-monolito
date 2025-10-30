package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Marca;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    @EntityGraph(attributePaths = {"materialSet.material", "materialSet.compSet.padrao"})
    List<Marca> findAll();

    @EntityGraph(attributePaths = {"materialSet.material", "materialSet.compSet.padrao"})
    Optional<Marca> findById(Long id);

    Optional<Marca> findByName(String name);
}