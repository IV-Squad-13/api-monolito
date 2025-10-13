package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface DocElementRepository<T extends DocElement> extends MongoRepository<T, String> {
    Optional<T> findByName(String name);
    Optional<T> findByNameAndEspecificacaoDoc(String name, EspecificacaoDoc especDoc);
    void deleteAllByName(@NotBlank String name);
}