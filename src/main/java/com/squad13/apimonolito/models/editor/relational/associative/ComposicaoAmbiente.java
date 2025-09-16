package com.squad13.apimonolito.models.editor.relational.associative;

import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_composicao_ambiente")
public class ComposicaoAmbiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empreendimento")
    private Empreendimento empreendimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_item_ambiente")
    private ItemAmbiente ambienteCompositor;
}