package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.MarcaDoc;
import com.squad13.apimonolito.models.revision.mongo.MarcaRevDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaRevDocRepository extends ElementRevDocRepository<MarcaRevDoc> {
    MarcaRevDoc findByMarcaAndRevisaoId(MarcaDoc marca, long l);
}
