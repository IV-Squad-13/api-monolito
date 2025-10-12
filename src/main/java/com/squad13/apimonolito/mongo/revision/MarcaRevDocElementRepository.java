package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.models.revision.mongo.MarcaRevDocElement;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaRevDocElementRepository extends RevDocElementRepository<MarcaRevDocElement> {
    MarcaRevDocElement findByMarcaAndRevisaoId(MarcaDocElement marca, long l);
}
