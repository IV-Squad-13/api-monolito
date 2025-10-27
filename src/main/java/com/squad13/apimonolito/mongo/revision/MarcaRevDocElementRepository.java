package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.models.revision.mongo.MarcaRevDocElement;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarcaRevDocElementRepository extends RevDocElementRepository<MarcaRevDocElement> {
    Optional<MarcaRevDocElement> findByRevisedDocIdAndRevisionId(ObjectId revisedDocId, Long revisionId);
}
