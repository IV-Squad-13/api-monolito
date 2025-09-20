package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente,Long> { }