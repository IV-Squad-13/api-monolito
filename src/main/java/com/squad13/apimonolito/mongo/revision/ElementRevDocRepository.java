package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.editor.structures.ElementDoc;
import com.squad13.apimonolito.models.revision.structures.ElementRevDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ElementRevDocRepository <T extends ElementRevDoc> extends MongoRepository<T, String> { }