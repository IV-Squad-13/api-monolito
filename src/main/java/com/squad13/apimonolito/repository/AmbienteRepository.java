package com.squad13.apimonolito.repository;

import com.squad13.apimonolito.models.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente,Long> {
    @Query("""
            SELECT DISTINCT a
            FROM Ambiente a
            LEFT JOIN FETCH a.itemSet ia
            LEFT JOIN FETCH ia.item i
            WHERE a.isActive = true AND ia.isActive = true AND i.isActive = true
            ORDER BY a.name, i.name
            """)
    List<Ambiente> findAmbientesItens();
}
