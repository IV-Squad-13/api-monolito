package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.util.enums.LocalEnum;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalDocRepository extends MongoRepository<LocalDoc, ObjectId> {
    List<LocalDoc> findByLocal(LocalEnum localEnum);

    Optional<LocalDoc> findByLocalAndEspecificacaoId(LocalEnum local, ObjectId especificacaoId);
}