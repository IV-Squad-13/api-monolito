package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDocElement;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRevDocElementRepository extends RevDocElementRepository<ItemRevDocElement> {
    Optional<ItemRevDocElement> findByRevisedDocIdAndRevisionId(ObjectId revisedDocId, Long revisionId);
}
