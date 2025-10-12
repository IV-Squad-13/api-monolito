package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDocElement;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRevDocElementRepository extends RevDocElementRepository<ItemRevDocElement> {
    ItemRevDocElement findByItemAndRevisaoId(ItemDocElement item, long l);
}
