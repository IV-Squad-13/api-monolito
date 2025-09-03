package com.squad13.apimonolito.repository;


import com.squad13.apimonolito.models.Padrao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PadraoRepository extends JpaRepository<Padrao, Long> {}
