package com.squad13.apimonolito.models.user.associative;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.util.enums.AccessEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_usuario_empreendimento")
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEmpreendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_empreendimento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empreendimento", nullable = false)
    @JsonBackReference
    private Empreendimento empreendimento;

    @Column(name = "tp_access", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccessEnum accessLevel;
}
