package com.squad13.apimonolito.models.catalog.associative;

import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.Item;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_item")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ambiente")
    private Ambiente ambiente;

    @Column(name = "is_obrigatorio")
    private Boolean isRequired;
}