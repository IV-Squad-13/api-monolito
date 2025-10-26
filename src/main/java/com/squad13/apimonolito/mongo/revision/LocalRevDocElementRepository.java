package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.models.revision.mongo.LocalRevDocElement;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalRevDocElementRepository extends RevDocElementRepository<LocalRevDocElement> {
    Optional<LocalRevDocElement> findByLocalDocIdAndRevisionId(ObjectId localDocId, Long revisionId);

    void deleteByLocalDocIdAndRevisionId(ObjectId localDocId, Long revisionId);
}
