package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDocElementRepository extends DocElementRepository<ItemDocElement> {
    boolean existsByNameAndDescAndTypeAndParentId(String name, String desc, String type, ObjectId parentId);
}