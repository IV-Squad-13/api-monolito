package com.squad13.apimonolito.models;

import com.squad13.apimonolito.models.associative.ItemAmbiente;
import com.squad13.apimonolito.models.associative.ItemComposto;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Entity
@Table(name = "tb_item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long id;

    @Column(name = "nm_item", unique = true, nullable = false, length = 40)
    private String name;

    @Column(name = "is_ativo")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_item_id")
    private TipoItem type;

    @Column(name = "vl_largura", length = 20)
    private String width;

    @Column(name = "vl_altura", length = 20)
    private String height;

    @Column(name = "ds_item", length = 60, nullable = false)
    private String desc;

    @OneToMany(mappedBy = "item")
    private Set<ItemComposto> itemCompostoSet;
}
