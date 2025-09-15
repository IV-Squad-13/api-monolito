package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.util.enums.LocalEnum;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalDocRepository extends ElementDocRepository<LocalDoc> {
    List<LocalDoc> findByLocal(LocalEnum localEnum);
}