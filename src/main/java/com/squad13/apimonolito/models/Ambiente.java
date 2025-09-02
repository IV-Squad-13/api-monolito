package com.squad13.apimonolito.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_ambiente")
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ambiente")
    private Long id;

    @Column(name = "nm_ambiente", length = 50)
    private String nome;

    @Column(name = "is_ativo")
    private Boolean ativo;
}
