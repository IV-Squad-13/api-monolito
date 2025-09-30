package com.squad13.apimonolito.models.catalog;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.squad13.apimonolito.models.catalog.associative.ComposicaoAmbiente;
import com.squad13.apimonolito.models.catalog.associative.ComposicaoMaterial;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tb_padrao")
public class Padrao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_padrao")
    private Long id;

    @Column(name = "nm_padrao", unique = true, nullable = false, length = 80)
    private String name;

    @Column(name = "is_ativo")
    private Boolean isActive;

    @OneToMany(mappedBy = "padrao",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<ComposicaoAmbiente> ambienteSet;

    @OneToMany(mappedBy = "padrao",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<ComposicaoMaterial> materialSet;

    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    private Set<Empreendimento> empreendimentoSet;

    @PreRemove
    private void setEmpreendimentoNull() {
        empreendimentoSet.forEach(itemDesc -> {
            itemDesc.setPadrao(null);
        });
    }
}