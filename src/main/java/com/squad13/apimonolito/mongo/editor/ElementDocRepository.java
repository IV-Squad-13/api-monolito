package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.structures.ElementDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ElementDocRepository<T extends ElementDoc> extends MongoRepository<T, String> { }