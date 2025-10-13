package com.squad13.apimonolito.mongo.editor;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.util.enums.LocalEnum;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmbienteDocElementRepository extends DocElementRepository<AmbienteDocElement> {
    Optional<AmbienteDocElement> findByNameAndLocalAndEspecificacaoDoc(String name, LocalEnum local, EspecificacaoDoc especificacaoDoc);
}