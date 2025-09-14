package com.squad13.apimonolito.util;

import com.squad13.apimonolito.models.editor.structures.ElementDoc;
import com.squad13.apimonolito.models.review.structures.ElementRevDoc;
import com.squad13.apimonolito.mongo.editor.ElementDocRepository;
import com.squad13.apimonolito.mongo.review.ElementRevDocRepository;
import com.squad13.apimonolito.util.annotations.MongoEntityType;
import lombok.Getter;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Getter
public class MongoRepositoryNavigator {

    private final Map<String, ElementDocRepository<? extends ElementDoc>> docRepositoryMap = new HashMap<>();
    private final Map<String, ElementRevDocRepository<? extends ElementRevDoc>> revDocRepositoryMap = new HashMap<>();

    public MongoRepositoryNavigator(
            List<ElementDocRepository<? extends ElementDoc>> docRepositoryList,
            List<ElementRevDocRepository<? extends ElementRevDoc>> revDocRepositoryList
    ) {
        docRepositoryList.forEach(repo -> {
            String type = extractEntityType(getRepositoryDomainClass(repo));
            docRepositoryMap.put(type, repo);
        });

        revDocRepositoryList.forEach(repo -> {
            String type = extractEntityType(getRepositoryDomainClass(repo));
            revDocRepositoryMap.put(type, repo);
        });
    }

    private String extractEntityType(Class<?> entityClass) {
        MongoEntityType annotation = entityClass.getAnnotation(MongoEntityType.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Entidade " + entityClass + " não possuí a anotação @MongoCatalogElementType");
        }
        return annotation.value();
    }

    private Class<?> getRepositoryDomainClass(MongoRepository<?, ?> repository) {
        return Optional.ofNullable(
                        GenericTypeResolver.resolveTypeArguments(repository.getClass(), MongoRepository.class)
                )
                .filter(args -> args.length > 0)
                .map(args -> args[0])
                .orElseThrow(() -> new IllegalStateException(
                        "O repositório não possuí um domínio válida: " + repository.getClass().getName()
                ));
    }
}