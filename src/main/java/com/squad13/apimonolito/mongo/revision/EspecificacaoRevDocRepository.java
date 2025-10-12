package com.squad13.apimonolito.mongo.revision;

import com.squad13.apimonolito.models.revision.mongo.EspecificacaoRevDoc;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspecificacaoRevDocRepository extends ElementRevDocRepository<EspecificacaoRevDoc> {
    List<EspecificacaoRevDoc> findByRevisaoId(Long revisaoId);
}