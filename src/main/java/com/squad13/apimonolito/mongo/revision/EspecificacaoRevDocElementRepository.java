package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.revision.mongo.EspecificacaoRevDocElement;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspecificacaoRevDocElementRepository extends RevDocElementRepository<EspecificacaoRevDocElement> {
    List<EspecificacaoRevDocElement> findByRevisaoId(Long revisaoId);
}