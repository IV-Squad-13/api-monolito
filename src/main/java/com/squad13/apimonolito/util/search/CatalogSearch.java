package com.squad13.apimonolito.util.search;

import com.squad13.apimonolito.exceptions.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CatalogSearch {

    private final EntityManager em;

    public <T> T findInCatalog(Long id, Class<T> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.select(root).where(cb.equal(root.get("id"), id));

        return em.createQuery(query).getResultStream()
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(clazz.getSimpleName() + " não encontrado no catálogo (id=" + id + ")")
                );
    }

    public <T> List<T> findByCriteria(
            Map<String, Object> filters,
            Class<T> clazz,
            String... fetches
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        for (String f : fetches) {
            root.fetch(f, JoinType.LEFT);
        }

        Predicate[] predicates = filters.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(entry -> {
                    Object value = entry.getValue();
                    return (value instanceof String strValue)
                            ? cb.like(cb.lower(root.get(entry.getKey())), "%" + strValue.toLowerCase() + "%")
                            : cb.equal(root.get(entry.getKey()), value);
                })
                .toArray(Predicate[]::new);

        query.select(root).where(predicates).distinct(true);

        return em.createQuery(query).getResultList();
    }
}