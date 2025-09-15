package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.ItemDoc;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDocRepository extends ElementDocRepository<ItemDoc> { }