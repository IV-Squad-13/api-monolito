package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "locais")
@CompoundIndex(
        name = "local_unique",
        def = "{'local': 1, 'especificacaoId': 1}",
        unique = true
)
public class LocalDoc {

    @MongoId(FieldType.OBJECT_ID)
    private ObjectId id;

    @NotNull
    @Indexed
    private ObjectId especificacaoId;

    private LocalEnum local;

    @Indexed
    private List<ObjectId> ambienteIds = new ArrayList<>();
}
