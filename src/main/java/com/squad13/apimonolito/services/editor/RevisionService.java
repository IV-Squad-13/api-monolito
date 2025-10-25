package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.mongo.revision.EspecificacaoRevDocElementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RevisionService {

    private final EspecificacaoRevDocElementRepository specRevRepository;


}