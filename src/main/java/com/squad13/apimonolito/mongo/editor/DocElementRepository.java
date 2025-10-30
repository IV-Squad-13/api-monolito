package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.structures.DocElement;
import jakarta.validation.constraints.NotBlank;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface DocElementRepository<T extends DocElement> extends MongoRepository<T, ObjectId> {
    Optional<T> findByName(String name);

    Optional<T> findByNameAndEspecificacaoId(String name, String especificacaoId);

    void deleteAllByName(@NotBlank String name);

    void deleteAllByEspecificacaoId(String especificacaoId);
}