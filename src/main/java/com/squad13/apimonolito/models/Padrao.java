package com.squad13.apimonolito.models;

import com.squad13.apimonolito.models.associative.AmbientePadrao;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity(name = "tb_padrao")
public class Padrao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, name = "nm_padrao", unique = true, nullable = false)
    private String name;

    @Column(name = "is_ativo")
    private Boolean isActive;

    @OneToMany(mappedBy = "padrao")
    private Set<AmbientePadrao> ambienteSet;
}