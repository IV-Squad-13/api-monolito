package com.squad13.apimonolito.util.search;

import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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

    public <T> List<T> findByCriteria(Map<String, Object> filters, Class<T> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        Predicate[] predicates = filters.entrySet().stream()
                .map(entry -> cb.equal(root.get(entry.getKey()), entry.getValue()))
                .toArray(Predicate[]::new);

        query.select(root).where(predicates);

        return em.createQuery(query).getResultList();
    }
}