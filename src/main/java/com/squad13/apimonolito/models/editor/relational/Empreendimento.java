package com.squad13.apimonolito.models.editor.relational;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.models.revision.relational.ProcessoHistorico;
import com.squad13.apimonolito.models.user.associative.UsuarioEmpreendimento;
import com.squad13.apimonolito.util.Auditable;
import com.squad13.apimonolito.util.enums.DocInitializationEnum;
import com.squad13.apimonolito.util.enums.EmpStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tb_empreendimento")
public class Empreendimento extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empreendimento")
    private Long id;

    @Column(name = "nm_empreendimento", unique = true, nullable = false, length = 80)
    private String name;

    @Column(name = "tp_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EmpStatusEnum status;

    @Column(name = "tp_init", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DocInitializationEnum init;

    @Column(name = "id_reference_doc", length = 24)
    private ObjectId referenceDocId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_padrao")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Padrao padrao;

    @OneToMany(mappedBy = "empreendimento",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<UsuarioEmpreendimento> usuarioSet = new HashSet<>();

    @OneToMany(mappedBy = "emp", fetch = FetchType.LAZY)
    private Set<ProcessoHistorico> processes = new HashSet<>();
}