package com.squad13.apimonolito.models.editor.relational.associative;

import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.util.AssociativeId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_composicao_material")
public class ComposicaoMaterial {

    @EmbeddedId
    private AssociativeId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("relA")
    @JoinColumn(name = "id_empreendimento")
    private Empreendimento empreendimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "id_marca", referencedColumnName = "id_marca"),
            @JoinColumn(name = "id_material", referencedColumnName = "id_material")
    })
    private MarcaMaterial materialCompositor;
}
