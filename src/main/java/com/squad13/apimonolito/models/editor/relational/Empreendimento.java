package com.squad13.apimonolito.models.editor.relational;

import com.squad13.apimonolito.models.catalog.Catalogo;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.util.enums.EmpreendimentoStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "tb_empreendimento")
public class Empreendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empreendimento")
    private Long id;

    @Column(name = "nm_empreendimento", unique = true, nullable = false, length = 80)
    private String name;

    @Column(name = "tp_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EmpreendimentoStatusEnum statusEnum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Catalogo catalogo;
}