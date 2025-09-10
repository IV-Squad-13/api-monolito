package com.squad13.apimonolito.models;

import com.squad13.apimonolito.models.associative.MarcaMaterial;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "tb_marca")
public class Marca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca")
    private Long id;

    @Column(name = "nm_marca", unique = true, nullable = false, length = 40)
    private String name;

    @Column(name = "is_ativo")
    private Boolean isActive;

    @OneToMany(mappedBy = "marca")
    private Set<MarcaMaterial> materialSet;
}
