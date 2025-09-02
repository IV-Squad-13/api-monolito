package com.squad13.apimonolito.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_material")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_material")
    private Long id;

    @Column(name = "nm_material", unique = true, nullable = false, length = 40)
    private String name;

    @Column(name = "is_ativo")
    private Boolean isActive;
}
