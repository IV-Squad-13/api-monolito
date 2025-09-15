package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.models.revision.mongo.LocalRevDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRevDocRepository extends ElementRevDocRepository<LocalRevDoc> {
    LocalRevDoc findByLocalAndRevisaoId(LocalDoc local, long l);

    void deleteByLocalAndRevisaoId(LocalDoc local, long l);
}
