package com.squad13.apimonolito.repository;

import com.squad13.apimonolito.models.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarcaRepository extends JpaRepository<Marca,Long> {
    @Query("""
            SELECT ma
            FROM Marca
            WHERE ma.isActive = true
            GROUP BY ma.name
            """)
    List<Marca> findMarcas();
}
