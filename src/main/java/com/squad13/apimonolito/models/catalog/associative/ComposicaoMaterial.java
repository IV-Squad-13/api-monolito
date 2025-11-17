package com.squad13.apimonolito.models.catalog.associative;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.util.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "tb_composicao_material",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_padrao", "id_marca_material"})
)
public class ComposicaoMaterial extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_composicao_material")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_padrao", nullable = false)
    @JsonBackReference
    private Padrao padrao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_marca_material", nullable = false)
    @JsonBackReference
    private MarcaMaterial compositor;
}
