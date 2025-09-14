package com.squad13.apimonolito.models.review.relational;

import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_revisao")
public class Revisao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_revisao")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "id_empreendimento")
    private Empreendimento empreendimento;

    @Column(name = "tp_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RevisaoStatusEnum statusEnum;
}