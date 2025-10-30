package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.ItemType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<ItemDesc, Long> {
    @EntityGraph(attributePaths = {"type", "ambienteSet.ambiente", "ambienteSet.compSet.padrao"})
    List<ItemDesc> findAll();

    @EntityGraph(attributePaths = {"type", "ambienteSet.ambiente", "ambienteSet.compSet.padrao"})
    Optional<ItemDesc> findById(Long id);

    Optional<ItemDesc> findByNameAndDescAndType(String name, String desc, ItemType type);
}