package com.squad13.apimonolito.models.editor.relational.associative;

import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.util.AssociativeId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_composicao_ambiente")
public class ComposicaoAmbiente {

    @EmbeddedId
    private AssociativeId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("relA")
    @JoinColumn(name = "id_empreendimento")
    private Empreendimento empreendimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "id_item", referencedColumnName = "id_item"),
            @JoinColumn(name = "id_ambiente", referencedColumnName = "id_ambiente")
    })
    private ItemAmbiente ambienteCompositor;
}