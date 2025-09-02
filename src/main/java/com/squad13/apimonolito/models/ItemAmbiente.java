package com.squad13.apimonolito.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_item_ambiente")
public class ItemAmbiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_ambiente")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ambiente", nullable = false)
    private Ambiente ambiente;

    @Column(name = "is_ativo")
    private Boolean ativo;
}
