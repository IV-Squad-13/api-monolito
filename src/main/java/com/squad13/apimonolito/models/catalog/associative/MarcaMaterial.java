package com.squad13.apimonolito.models.catalog.associative;

import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.util.AssociativeId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_marca_material")
public class MarcaMaterial {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "relA", column = @Column(name = "id_marca")),
            @AttributeOverride(name = "relB", column = @Column(name = "id_material"))
    })
    private AssociativeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("relA")
    @JoinColumn(name = "id_marca", nullable = false)
    private Marca marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("relB")
    @JoinColumn(name = "id_material", nullable = false)
    private Material material;

    @Column(name = "is_obrigatorio")
    private boolean isRequired;
}