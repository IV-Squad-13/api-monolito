package com.squad13.apimonolito.util.mapper;

import com.squad13.apimonolito.DTO.editor.res.ResDocElementDTO;
import com.squad13.apimonolito.DTO.editor.res.ResLocalDocDTO;
import com.squad13.apimonolito.DTO.editor.res.ResSpecDTO;
import com.squad13.apimonolito.DTO.revision.LoadRevDocParamsDTO;
import com.squad13.apimonolito.DTO.revision.res.ResLocalRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDocDTO;
import com.squad13.apimonolito.DTO.revision.res.ResSpecRevDTO;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.models.revision.mongo.EspecificacaoRevDocElement;
import com.squad13.apimonolito.models.revision.mongo.LocalRevDocElement;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
public class RevisionMapper {

    public <D extends ResDocElementDTO, R extends RevDocElement> R toRevDoc(D doc, Long revisionId, Supplier<R> factory) {
        R rev = factory.get();
        rev.setRevisedDocId(doc.getId());
        rev.setRevisionId(revisionId);
        rev.setIsApproved(null);
        rev.setComment(null);
        return rev;
    }
}