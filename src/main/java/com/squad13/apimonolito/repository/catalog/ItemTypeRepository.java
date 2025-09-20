package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {
    Optional<ItemType> findByIdOrName(Long id, String name);
}