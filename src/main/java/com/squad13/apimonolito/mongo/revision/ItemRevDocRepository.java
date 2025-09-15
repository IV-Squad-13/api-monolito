package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.revision.mongo.ItemRevDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRevDocRepository extends ElementRevDocRepository<ItemRevDoc> { }
