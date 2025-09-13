package com.squad13.apimonolito.repository.editor.mongo;

import com.squad13.apimonolito.models.editor.structures.DocElement;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocElementRepository extends MongoRepository<DocElement, String> {
}
