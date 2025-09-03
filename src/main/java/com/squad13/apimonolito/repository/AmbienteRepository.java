package com.squad13.apimonolito.repository;

import com.squad13.apimonolito.models.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente,Long> {}
