package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.revision.mongo.AmbienteRevDocElement;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbienteRevDocElementRepository extends RevDocElementRepository<AmbienteRevDocElement> {
    AmbienteRevDocElement findByAmbienteAndRevisaoId(@NotNull AmbienteDocElement ambiente, @NotNull Long revisaoId);

    void deleteByAmbienteAndRevisaoId(AmbienteDocElement ambiente, long l);
}
