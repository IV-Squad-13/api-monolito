package com.squad13.apimonolito.models.catalog;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(
        name = "tb_ambiente",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nm_ambiente", "tp_local"})
        }
)
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ambiente")
    private Long id;

    @Column(name = "nm_ambiente", nullable = false, length = 80)
    private String name;

    @Column(name = "tp_local", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LocalEnum local;

    @Column(name = "is_ativo")
    private Boolean isActive;

    @OneToMany(mappedBy = "ambiente",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<ItemAmbiente> itemSet;
}