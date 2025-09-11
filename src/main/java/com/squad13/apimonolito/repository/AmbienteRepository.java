package com.squad13.apimonolito.repository;

import com.squad13.apimonolito.models.catalog.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbienteRepository extends CatalogRepository<Ambiente,Long> {}
