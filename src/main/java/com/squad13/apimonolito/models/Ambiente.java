package com.squad13.apimonolito.models;

import com.squad13.apimonolito.DTO.AmbienteDTO;
import com.squad13.apimonolito.models.associative.AmbientePadrao;
import com.squad13.apimonolito.models.associative.ItemAmbiente;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
@Data
@Entity
@Table(name = "tb_ambiente")
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ambiente")
    private Long id;

    @Column(name = "nm_ambiente", unique = true, nullable = false, length = 40)
    private String name;

    @Column(name = "is_ativo")
    private Boolean isActive;

    @OneToMany(mappedBy = "ambiente")
    private Set<ItemAmbiente> itemAmbiente;

    @OneToMany(mappedBy = "ambiente")
    private Set<AmbientePadrao> padraoSet;


}
