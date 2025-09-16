package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.MaterialDoc;
import com.squad13.apimonolito.models.revision.mongo.MarcaRevDoc;
import com.squad13.apimonolito.models.revision.mongo.MaterialRevDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRevDocRepository extends ElementRevDocRepository<MaterialRevDoc> {
    MaterialRevDoc findByMaterialAndRevisaoId(MaterialDoc material, long l);

    void deleteByMaterialAndRevisaoId(MaterialDoc material, long l);
}