package com.squad13.apimonolito.models.catalog;

import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "tb_material")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_material")
    private Long id;

    @Column(name = "nm_material", unique = true, nullable = false, length = 80)
    private String name;

    @Column(name = "is_ativo")
    private Boolean isActive;

    @OneToMany(mappedBy = "material")
    private Set<MarcaMaterial> marcaSet;
}
