package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaDocElementRepository extends DocElementRepository<MarcaDocElement> {
    boolean existsByNameAndParentId(String name, ObjectId parentId);
}