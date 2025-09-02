package com.squad13.apimonolito.models.associative;

import com.squad13.apimonolito.models.Ambiente;
import com.squad13.apimonolito.models.Item;
import com.squad13.apimonolito.util.AssociativeId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_item_ambiente")
public class ItemAmbiente {

    @EmbeddedId
    private AssociativeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item_composto", nullable = false)
    private ItemComposto item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ambiente", nullable = false)
    private Ambiente ambiente;

    @Column(name = "is_ativo")
    private Boolean isActive;
}
