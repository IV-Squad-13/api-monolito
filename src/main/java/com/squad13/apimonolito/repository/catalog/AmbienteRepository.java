package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente,Long> {
    @EntityGraph(attributePaths = {"itemSet"})
    List<Ambiente> findAll();

    Optional<Ambiente> findByNameAndLocal(String name, LocalEnum local);
}