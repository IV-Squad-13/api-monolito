package com.squad13.apimonolito.models.catalog.associative;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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
    @JoinColumn(name = "id_item_desc", nullable = false)
    @JsonBackReference
    private ItemDesc itemDesc;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ambiente", nullable = false)
    @JsonBackReference
    private Ambiente ambiente;

    @OneToMany(mappedBy = "compositor",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<ComposicaoAmbiente> compSet = new HashSet<>();
}
