package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<ItemDesc, Long> {

    Optional<Object> findByNameAndDescAndType(String name, String desc, ItemType type);

    Optional<ItemType> findByName(String name);
}