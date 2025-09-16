package com.squad13.apimonolito.models.editor.relational.associative;

import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_composicao_material")
public class ComposicaoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_composicao_material")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empreendimento")
    private Empreendimento empreendimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_marca_material")
    private MarcaMaterial materialCompositor;
}
