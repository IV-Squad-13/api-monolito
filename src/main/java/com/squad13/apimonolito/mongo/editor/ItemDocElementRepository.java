package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDocElementRepository extends DocElementRepository<ItemDocElement> {
    boolean existsByNameAndDescAndTypeAndParentId(@NotBlank @Size(max = 100) String name, @NotBlank String desc, String type, String parentId);
}