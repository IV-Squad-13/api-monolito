package com.squad13.apimonolito.repository;

import com.squad13.apimonolito.models.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material,Long> {
    @Query("""
            SELECT m
            FROM Material m
            WHERE m.isActive = true
            ORDER BY m.name
            """)
    List<Material> findMateriais();
}
