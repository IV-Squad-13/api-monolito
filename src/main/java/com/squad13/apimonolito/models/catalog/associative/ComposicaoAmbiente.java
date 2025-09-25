package com.squad13.apimonolito.models.catalog.associative;

import com.squad13.apimonolito.models.catalog.Padrao;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
        name = "tb_composicao_ambiente",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_padrao", "id_item_ambiente"})
)
public class ComposicaoAmbiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_composicao_ambiente")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_padrao")
    private Padrao padrao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_item_ambiente")
    private ItemAmbiente compositor;
}