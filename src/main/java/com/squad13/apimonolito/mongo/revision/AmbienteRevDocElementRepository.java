package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.revision.mongo.AmbienteRevDocElement;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmbienteRevDocElementRepository extends RevDocElementRepository<AmbienteRevDocElement> {
    Optional<AmbienteRevDocElement> findByAmbienteDocIdAndRevisionId(ObjectId ambienteDocId, Long revisionId);

    void deleteByAmbienteDocIdAndRevisionId(ObjectId ambienteDocId, Long revisionId);
}
