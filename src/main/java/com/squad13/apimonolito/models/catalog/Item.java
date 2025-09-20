package com.squad13.apimonolito.models.catalog;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(
        name = "tb_item",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nm_item", "ds_item"})
        }
)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long id;

    @Column(name = "nm_item", nullable = false, length = 80)
    private String name;

    @Column(name = "is_ativo")
    private Boolean isActive;

    @Column(name = "ds_item", length = 240, nullable = false)
    private String desc;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<ItemAmbiente> ambienteSet;
}