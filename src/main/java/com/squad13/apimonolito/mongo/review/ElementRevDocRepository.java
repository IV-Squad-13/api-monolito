package com.squad13.apimonolito.mongo.review;

import com.squad13.apimonolito.models.review.structures.ElementRevDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ElementRevDocRepository <T extends ElementRevDoc> extends MongoRepository<T, String> { }