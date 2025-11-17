package com.squad13.apimonolito.models.revision.relational;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.util.Auditable;
import com.squad13.apimonolito.util.enums.ProcActionEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "tb_processo_historico",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_empreendimento", "id_revisao", "id_processo_origem"})
        }
)
public class ProcessoHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_processo_historico")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empreendimento")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Empreendimento emp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_revisao")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Revisao revision;

    @Column(name = "tp_acao", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ProcActionEnum procAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo_origem")
    private ProcessoHistorico origin;

    /*
    TODO: Referenciar documento gerado em registros de processos aprovados
    */
    @Column(name = "id_bin")
    private Long binId;

    @CreationTimestamp
    @Column(name = "dt_comeco", updatable = false, nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant started;

    @Column(name = "dt_fim")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant finished;

    @OneToMany(mappedBy = "origin", fetch = FetchType.LAZY)
    private List<ProcessoHistorico> derivedProcesses = new ArrayList<>();
}