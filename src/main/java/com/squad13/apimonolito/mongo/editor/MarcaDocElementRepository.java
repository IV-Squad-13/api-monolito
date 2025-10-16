package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaDocElementRepository extends DocElementRepository<MarcaDocElement> {
    boolean existsByNameAndPrevIdAndEspecificacaoDoc(String name, String prevId, EspecificacaoDoc especificacaoDoc);
}