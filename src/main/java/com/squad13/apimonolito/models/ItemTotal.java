package com.squad13.apimonolito.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_item_total")
public class ItemTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_total")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_material")
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca")
    private Marca marca;

    @Column(name = "is_ativo")
    private Boolean ativo;
}
