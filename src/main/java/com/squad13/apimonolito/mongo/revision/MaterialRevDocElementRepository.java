package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import com.squad13.apimonolito.models.revision.mongo.MaterialRevDocElement;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaterialRevDocElementRepository extends RevDocElementRepository<MaterialRevDocElement> {
    Optional<MaterialRevDocElement> findByMaterialDocIdAndRevisionId(ObjectId materialDocId, Long revisionId);

    void deleteByMaterialDocIdAndRevisionId(ObjectId materialDocId, Long revisionId);
}