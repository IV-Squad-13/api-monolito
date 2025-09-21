package com.squad13.apimonolito.models.catalog;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tb_item_type")
public class ItemType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_type")
    private Long id;

    @Column(name = "nm_item_type", length = 60, unique = true, nullable = false)
    private String name;

    @Column(name = "is_ativo")
    private Boolean isActive;

    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    private Set<ItemDesc> itemDescSet;

    @PreRemove
    private void setItemDescNull() {
        itemDescSet.forEach(itemDesc -> {
            itemDesc.setType(null);
        });
    }
}