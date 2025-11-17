package com.squad13.apimonolito.models.revision.relational;

import com.squad13.apimonolito.util.Auditable;
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
public class Revisao extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_revisao")
    private Long id;

    @Column(name = "tp_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RevisaoStatusEnum status;

    @Column(name = "ds_conclusao", length = 240)
    private String conclusion;

    @Column(name = "tp_regra", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RevisionRule rule;

    @OneToMany(mappedBy = "revision", fetch = FetchType.LAZY)
    private List<ProcessoHistorico> processes = new ArrayList<>();
}