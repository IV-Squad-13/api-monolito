package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material,Long> {
    @EntityGraph(attributePaths = {"marcaSet"})
    List<Material> findAll();

    Optional<Material> findByName(String name);
}