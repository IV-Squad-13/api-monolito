package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.exceptions.InvalidStageException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.models.revision.mongo.EspecificacaoRevDocElement;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.revision.EspecificacaoRevDocElementRepository;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.repository.revision.ProcessoHistoricoRepository;
import com.squad13.apimonolito.repository.revision.RevisaoRepository;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.enums.EmpStatusEnum;
import com.squad13.apimonolito.util.enums.RevDocElementEnum;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;
import com.squad13.apimonolito.util.search.DocumentSearch;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component("require")
@RequiredArgsConstructor
public class EmpreendimentoBlocker {

    private final EmpreendimentoRepository empRepository;

    private final EspecificacaoDocRepository specRepository;

    private final DocumentSearch docSearch;
    private final RevisaoRepository revisaoRepository;
    private final EspecificacaoRevDocElementRepository especificacaoRevDocElementRepository;

    public boolean editingStage(Long id) {
        Empreendimento emp = empRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empreendimento não encontrado: " + id));

        if (!emp.getStatus().equals(EmpStatusEnum.ELABORACAO)) {
            throw new InvalidStageException("Não é possível realizar essa operação durante a etapa atual do processo");
        }

        return true;
    }

    public boolean editingStage(ObjectId id) {
        EspecificacaoDoc spec = specRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada: " + id));

        return editingStage(spec.getEmpreendimentoId());
    }

    public boolean editingStage(ObjectId id, DocElementEnum docType) {
        DocElement doc = docSearch.findInDocument(id, docType.getDocElement());

        EspecificacaoDoc spec = specRepository.findById(doc.getEspecificacaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada: " + id));

        return editingStage(spec.getEmpreendimentoId());
    }

    public boolean revisionStage(Long id) {
        Revisao rev = revisaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisão não encontrada: " + id));

        if (!rev.getStatus().equals(RevisaoStatusEnum.INICIADA)) {
            throw new InvalidStageException("Não é possível realizar essa operação durante a etapa atual do processo");
        }

        return true;
    }

    public boolean revisionStage(ObjectId id) {
        EspecificacaoRevDocElement revDoc = especificacaoRevDocElementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento de Revisão não encontrado: " + id));

        return revisionStage(revDoc.getRevisionId());
    }

    public boolean revisionStage(ObjectId id, RevDocElementEnum docType) {
        RevDocElement doc = docSearch.findInDocument(id, docType.getRevDocument());
        return revisionStage(doc.getRevisionId());
    }

    public boolean pendingRevisionStage(Long id) {
        Revisao rev = revisaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisão não encontrada: " + id));

        if (!rev.getStatus().equals(RevisaoStatusEnum.PENDENTE)) {
            throw new InvalidStageException("Não é possível realizar essa operação durante a etapa atual do processo");
        }

        return true;
    }
}