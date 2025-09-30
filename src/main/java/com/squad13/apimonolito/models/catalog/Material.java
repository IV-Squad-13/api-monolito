package com.squad13.apimonolito.models.catalog;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
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

    @OneToMany(mappedBy = "material",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<MarcaMaterial> marcaSet;
}
