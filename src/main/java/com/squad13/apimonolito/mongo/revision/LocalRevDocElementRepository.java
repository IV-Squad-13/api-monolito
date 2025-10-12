package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.LocalDocElement;
import com.squad13.apimonolito.models.revision.mongo.LocalRevDocElement;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRevDocElementRepository extends RevDocElementRepository<LocalRevDocElement> {
    LocalRevDocElement findByLocalAndRevisaoId(LocalDocElement local, long l);

    void deleteByLocalAndRevisaoId(LocalDocElement local, long l);
}
