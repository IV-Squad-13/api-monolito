package com.squad13.apimonolito.models.catalog.associative;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "tb_marca_material",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_marca", "id_material"})
)
public class MarcaMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca_material")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_marca")
    @JsonBackReference
    private Marca marca;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_material")
    @JsonBackReference
    private Material material;
}