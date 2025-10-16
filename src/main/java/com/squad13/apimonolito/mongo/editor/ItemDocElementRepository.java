package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDocElementRepository extends DocElementRepository<ItemDocElement> {
    boolean existsByNameAndPrevIdAndEspecificacaoDoc(String name, String prevId, EspecificacaoDoc especificacaoDoc);
}