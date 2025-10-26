package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Material;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    @EntityGraph(attributePaths = {"marcaSet.marca", "marcaSet.compSet.padrao"})
    List<Material> findAll();

    @EntityGraph(attributePaths = {"marcaSet.marca", "marcaSet.compSet.padrao"})
    Optional<Material> findById(Long id);

    Optional<Material> findByName(String name);
}