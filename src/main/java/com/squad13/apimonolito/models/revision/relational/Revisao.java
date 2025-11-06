package com.squad13.apimonolito.models.revision.relational;

import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;
import com.squad13.apimonolito.util.enums.rule.RevisionRule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    private RevisaoStatusEnum status;

    /*
    TODO: Adicionar um texto de conclusão da revisão

    private String conclusion;
    */

    @Column(name = "tp_regra", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RevisionRule rule;

    @CreationTimestamp
    @Column(name = "dt_criacao", updatable = false, nullable = false)
    private Instant created;

    @UpdateTimestamp
    @Column(name = "dt_atualizacao")
    private Instant updated;

    @OneToMany(mappedBy = "revision", fetch = FetchType.LAZY)
    private List<ProcessoHistorico> processes = new ArrayList<>();
}