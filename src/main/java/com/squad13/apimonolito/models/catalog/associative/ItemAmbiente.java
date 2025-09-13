package com.squad13.apimonolito.models.catalog.associative;

import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.Item;
import com.squad13.apimonolito.util.AssociativeId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_item_ambiente")
public class ItemAmbiente {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "relA", column = @Column(name = "id_item")),
            @AttributeOverride(name = "relB", column = @Column(name = "id_ambiente"))
    })
    private AssociativeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("relA")
    @JoinColumn(name = "id_item", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("relB")
    @JoinColumn(name = "id_ambiente", nullable = false)
    private Ambiente ambiente;

    @Column(name = "is_obrigatorio")
    private Boolean isRequired;
}