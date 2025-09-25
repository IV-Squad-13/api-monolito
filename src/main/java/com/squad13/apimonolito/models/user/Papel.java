package com.squad13.apimonolito.models.user;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_papel")
public class Papel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_papel")
    private Long id;

    @Column(name = "nm_papel",  nullable = false, length = 50)
    private String nome;
}
