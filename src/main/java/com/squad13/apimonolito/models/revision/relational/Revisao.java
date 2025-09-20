package com.squad13.apimonolito.models.revision.relational;

import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_revisao")
public class Revisao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_revisao")
    private Long id;

    @Column(name = "tp_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RevisaoStatusEnum statusEnum;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empreendimento")
    private Empreendimento empreendimento;
}