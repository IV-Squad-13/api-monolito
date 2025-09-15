package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDoc;
import com.squad13.apimonolito.models.revision.mongo.AmbienteRevDoc;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbienteRevDocRepository extends ElementRevDocRepository<AmbienteRevDoc> {
    AmbienteRevDoc findByAmbienteAndRevisaoId(@NotNull AmbienteDoc ambiente, @NotNull Long revisaoId);

    void deleteByAmbienteAndRevisaoId(AmbienteDoc ambiente, long l);
}
