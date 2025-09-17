package com.squad13.apimonolito.models.catalog;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
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
}
