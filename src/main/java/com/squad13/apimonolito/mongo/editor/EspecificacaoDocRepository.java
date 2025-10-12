package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspecificacaoDocRepository extends MongoRepository<EspecificacaoDoc, String> {
    List<EspecificacaoDoc> findByEmpreendimentoId(Long empreendimentoId);
}
