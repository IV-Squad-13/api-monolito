package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.revision.mongo.LocalRevDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRevDocRepository extends ElementRevDocRepository<LocalRevDoc> { }
