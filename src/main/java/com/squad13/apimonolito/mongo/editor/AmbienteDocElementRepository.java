package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbienteDocElementRepository extends DocElementRepository<AmbienteDocElement> {
    boolean existsByNameAndParentId(String name, ObjectId parentId);
}