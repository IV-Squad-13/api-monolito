package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.structures.DocElement;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DocElementRepository<T extends DocElement> extends MongoRepository<T, String> {
    T findByName(String name);
    void deleteAllByName(@NotBlank String name);
}