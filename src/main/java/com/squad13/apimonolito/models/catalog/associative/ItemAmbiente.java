package com.squad13.apimonolito.models.catalog.associative;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "tb_item_ambiente",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_item_desc", "id_ambiente"})
)
public class ItemAmbiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_ambiente")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_item_desc")
    @JsonBackReference
    private ItemDesc itemDesc;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ambiente")
    @JsonBackReference
    private Ambiente ambiente;

    @Column(name = "is_obrigatorio")
    private Boolean isRequired;
}