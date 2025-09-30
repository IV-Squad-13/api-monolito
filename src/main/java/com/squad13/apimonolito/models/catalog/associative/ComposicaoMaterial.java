package com.squad13.apimonolito.models.catalog.associative;

import com.squad13.apimonolito.models.catalog.Padrao;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
        name = "tb_composicao_material",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_padrao", "id_marca_material"})
)
public class ComposicaoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_composicao_material")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_padrao")
    private Padrao padrao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_marca_material")
    private MarcaMaterial compositor;
}
