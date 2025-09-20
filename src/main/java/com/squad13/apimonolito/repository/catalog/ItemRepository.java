package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> { }