package com.squad13.apimonolito.models.catalog;

import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Entity
@Table(
        name = "tb_ambiente",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nm_ambiente", "tp_local"})
        }
)
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ambiente")
    private Long id;

    @Column(name = "nm_ambiente", unique = true, nullable = false, length = 80)
    private String name;

    @Column(name = "tp_local", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LocalEnum local;

    @Column(name = "is_ativo")
    private Boolean isActive;

    @OneToMany(mappedBy = "ambiente")
    private Set<ItemAmbiente> itemAmbiente;
}
