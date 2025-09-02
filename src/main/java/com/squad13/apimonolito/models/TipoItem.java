package com.squad13.apimonolito.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "tb_tipo_item")
public class TipoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_item")
    private Long id;

    @Column(name = "nm_tipo_item", length = 50)
    private String nome;

    @Column(name = "is_ativo")
    private Boolean ativo;

    @OneToMany(mappedBy = "cdItem")
    private Set<Item> items;
}
