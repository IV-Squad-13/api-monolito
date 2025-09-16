package com.squad13.apimonolito.models.editor.relational;

import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.util.enums.EmpreendimentoStatusEnum;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_empreendimento")
public class Empreendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empreendimento")
    private Long id;

    @Column(name = "nm_empreendimento", unique = true, nullable = false, length = 40)
    private String name;

    @Column(name = "tp_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EmpreendimentoStatusEnum statusEnum;
}