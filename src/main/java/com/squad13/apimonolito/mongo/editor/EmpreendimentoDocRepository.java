package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.EmpreendimentoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpreendimentoDocRepository extends MongoRepository<EmpreendimentoDoc, String> { }
