package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.DTO.catalog.res.ResItemDTO;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemDesc, Long> {

    @EntityGraph(attributePaths = {"type", "ambienteSet"})
    List<ItemDesc> findAll();

    Optional<ItemDesc> findByNameAndDescAndType(String name, String desc, ItemType type);
}