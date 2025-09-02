package com.squad13.apimonolito.models.associative;

import com.squad13.apimonolito.models.Item;
import com.squad13.apimonolito.models.Marca;
import com.squad13.apimonolito.models.Material;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_item_composto")
public class ItemComposto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_composto")
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
    private Boolean isActive;
}
