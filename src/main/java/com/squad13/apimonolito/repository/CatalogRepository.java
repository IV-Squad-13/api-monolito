package com.squad13.apimonolito.repository;

import com.squad13.apimonolito.models.catalog.CatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface CatalogRepository<T extends CatalogEntity, ID extends Serializable> extends JpaRepository<T, ID> {
    Optional<T> findByName(String name);
}