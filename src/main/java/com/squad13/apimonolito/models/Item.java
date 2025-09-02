package com.squad13.apimonolito.models;

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

    @Column(name = "nm_item", length = 50)
    private String nome;

    @Column(name = "is_ativo")
    private Boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_item", referencedColumnName = "id_tipo_item", nullable = false)
    private TipoItem cdItem;

    @Column(name = "vl_largura")
    private BigDecimal largura;

    @Column(name = "vl_altura")
    private BigDecimal altura;

    @Column(name = "ds_item")
    private String dsItem;

    @OneToMany(mappedBy = "item")
    private Set<ItemTotal> itensTotais;

    @OneToMany(mappedBy = "item")
    private Set<ItemAmbiente> itemAmbientes;
}
