package com.squad13.apimonolito.repository.review;

import com.squad13.apimonolito.models.review.relational.Revisao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevisaoRepository extends JpaRepository<Revisao, Long> { }