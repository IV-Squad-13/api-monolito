package com.squad13.apimonolito.models.user;

import com.squad13.apimonolito.util.Auditable;
import com.squad13.apimonolito.util.enums.PapelEnum;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_papel")
public class Papel extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_papel")
    private Long id;

    @Column(name = "nm_papel", nullable = false, length = 50, unique = true)
    @Enumerated(EnumType.STRING)
    private PapelEnum nome;
}
