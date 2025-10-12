package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.LocalDocElement;
import com.squad13.apimonolito.util.enums.LocalEnum;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalDocElementRepository extends DocElementRepository<LocalDocElement> {
    List<LocalDocElement> findByLocal(LocalEnum localEnum);
}