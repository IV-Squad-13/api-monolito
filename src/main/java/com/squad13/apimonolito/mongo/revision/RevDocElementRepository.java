package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RevDocElementRepository<T extends RevDocElement> extends MongoRepository<T, String> {
}