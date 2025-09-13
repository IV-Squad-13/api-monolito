package com.squad13.apimonolito.models.editor.mongo;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "empreendimentos")
public class EmpreendimentoDoc {

    @Id
    private String id;

    @NotNull
    private Long empreendimentoId;

    @NotBlank
    @Size(max = 40)
    private String name;

    private String desc;

    private String obs;

    @DBRef
    private List<LocalDoc> locais;

    @DBRef
    private List<MaterialDoc> materiais;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;
}
