package com.squad13.apimonolito.util.search;

import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

    public <T> T findAssociativeInCatalog(
            Long sourceId,
            Long targetId,
            Class<T> associativeClass,
            String sourceAttr,
            String targetAttr
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(associativeClass);
        Root<T> root = query.from(associativeClass);

        Predicate predicate = cb.and(
                cb.equal(root.get(sourceAttr).get("id"), sourceId),
                cb.equal(root.get(targetAttr).get("id"), targetId)
        );

        query.select(root).where(predicate);

        return em.createQuery(query)
                .getResultStream()
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(associativeClass.getSimpleName() +
                                " not found for (" + sourceAttr + "=" + sourceId +
                                ", " + targetAttr + "=" + targetId + ")"));
    }
}