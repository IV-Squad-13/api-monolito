package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.ItemDoc;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDoc;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRevDocRepository extends ElementRevDocRepository<ItemRevDoc> {
    ItemRevDoc findByItemAndRevisaoId(ItemDoc item, long l);
}
