package com.squad13.apimonolito.models.catalog;

import jakarta.persistence.*;
import lombok.Data;

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

    @Column(name = "ds_item", length = 60, nullable = false)
    private String desc;
}
