package com.squad13.apimonolito.models.editor.relational;

import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.util.enums.EmpreendimentoStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "tb_especificacao")
public class Especificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especificacao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_padrao")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Padrao padrao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empreendimento")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Empreendimento empreendimento;
}