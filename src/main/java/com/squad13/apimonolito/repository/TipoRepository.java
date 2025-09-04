package com.squad13.apimonolito.repository;

import com.squad13.apimonolito.models.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRepository extends JpaRepository<TipoItem,Long> {
}
