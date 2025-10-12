package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import com.squad13.apimonolito.models.revision.mongo.MaterialRevDocElement;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRevDocElementRepository extends RevDocElementRepository<MaterialRevDocElement> {
    MaterialRevDocElement findByMaterialAndRevisaoId(MaterialDocElement material, long l);

    void deleteByMaterialAndRevisaoId(MaterialDocElement material, long l);
}