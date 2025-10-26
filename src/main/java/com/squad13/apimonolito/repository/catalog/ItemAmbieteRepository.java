package com.squad13.apimonolito.repository.catalog;

import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemAmbieteRepository extends JpaRepository<ItemAmbiente, Long> {
    @EntityGraph(attributePaths = {"itemDesc", "ambiente", "compSet"})
    List<ItemAmbiente> findAll();

    List<ItemAmbiente> findByAmbiente_Id(Long ambienteId);

    List<ItemAmbiente> findByItemDesc_Id(Long itemDescId);

    Optional<ItemAmbiente> findByItemDescAndAmbiente(ItemDesc itemDesc, Ambiente ambiente);

    List<ItemAmbiente> findByItemDesc_IdIn(List<Long> itemIds);
}