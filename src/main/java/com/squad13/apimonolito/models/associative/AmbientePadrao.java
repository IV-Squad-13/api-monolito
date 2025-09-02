package com.squad13.apimonolito.models.associative;

import com.squad13.apimonolito.models.Ambiente;
import com.squad13.apimonolito.models.Padrao;
import com.squad13.apimonolito.util.AssociativeId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_ambiente_padrao")
public class AmbientePadrao {

    @EmbeddedId
    private AssociativeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ambiente", nullable = false)
    private Ambiente ambiente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_padrao", nullable = false)
    private Padrao padrao;

    @Column(name = "is_ativo")
    private Boolean isActive;
}
