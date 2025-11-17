package com.squad13.apimonolito.models.catalog;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.util.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(
        name = "tb_item_desc",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nm_item_desc", "ds_item", "id_item_type"})
        }
)
public class ItemDesc extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_desc")
    private Long id;

    @Column(name = "nm_item_desc", length = 120)
    private String name;

    @Column(name = "is_ativo")
    private Boolean isActive;

    @Column(name = "ds_item", length = 320, nullable = false)
    private String desc;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_item_type")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JsonBackReference
    private ItemType type;

    @OneToMany(mappedBy = "itemDesc",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<ItemAmbiente> ambienteSet;
}
