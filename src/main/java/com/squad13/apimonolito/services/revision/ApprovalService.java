package com.squad13.apimonolito.services.revision;

import com.squad13.apimonolito.DTO.revision.LoadRevDocParamsDTO;
import com.squad13.apimonolito.DTO.revision.RevDocSearchParamsDTO;
import com.squad13.apimonolito.DTO.revision.edit.*;
import com.squad13.apimonolito.DTO.revision.res.*;
import com.squad13.apimonolito.exceptions.exceptions.InvalidStageException;
import com.squad13.apimonolito.exceptions.exceptions.PendingEvaluationException;
import com.squad13.apimonolito.exceptions.exceptions.PreviousProcessNotFoundException;
import com.squad13.apimonolito.exceptions.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.revision.mongo.*;
import com.squad13.apimonolito.models.revision.relational.ProcessoHistorico;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.revision.*;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.repository.revision.ProcessoHistoricoRepository;
import com.squad13.apimonolito.repository.revision.RevisaoRepository;
import com.squad13.apimonolito.util.enums.*;
import com.squad13.apimonolito.util.mapper.EditorMapper;
import com.squad13.apimonolito.util.search.DocumentSearch;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final EspecificacaoRevDocElementRepository especificacaoRevDocElementRepository;
    private final LocalRevDocElementRepository localRevDocElementRepository;
    private final AmbienteRevDocElementRepository ambienteRevDocElementRepository;
    private final ItemRevDocElementRepository itemRevDocElementRepository;
    private final MaterialRevDocElementRepository materialRevDocElementRepository;
    private final MarcaRevDocElementRepository marcaRevDocElementRepository;

    private final EspecificacaoDocRepository especificacaoDocRepository;

    private final DocumentSearch documentSearch;
    private final EditorMapper editorMapper;

    private final RevisionService revisionService;
    private final RevisaoRepository revisaoRepository;

    private final EmpreendimentoRepository empreendimentoRepository;
    private final ProcessoHistoricoRepository processoHistoricoRepository;

    private Revisao findByRevisaoById(Long id) {
        return revisaoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão não encontrada: " + id)
                );
    }

    private ProcessoHistorico findPreviousProcess(Revisao rev, ProcActionEnum action) {
        return rev.getProcesses().stream()
                .filter(p -> p.getFinished() == null && p.getProcAction().equals(action))
                .findFirst().orElseThrow(
                        () -> new PreviousProcessNotFoundException("Processo em aberto não encontrado para a Revisão de ID '" + rev.getId() + "' em " + rev.getStatus())
                );
    }

    @Transactional
    public ResRevDocDTO update(ObjectId id, EditRevDocDTO dto) {
        return switch (dto.getDocType()) {
            case ESPECIFICACAO -> updateEspecificacao(id, (EditSpecRevDocDTO) dto);
            case LOCAL -> updateLocal(id, (EditLocalRevDocDTO) dto);
            case AMBIENTE -> updateAmbiente(id, (EditAmbRevDocDTO) dto);
            case ITEM -> updateItem(id, (EditItemRevDocDTO) dto);
            case MATERIAL -> updateMaterial(id, (EditMatRevDocDTO) dto);
            case MARCA -> updateMarca(id, (EditMarRevDocDTO) dto);
        };
    }

    private ResSpecRevDTO updateEspecificacao(ObjectId id, EditSpecRevDocDTO dto) {
        EspecificacaoRevDocElement specRev = especificacaoRevDocElementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisão de Especificação não encontrada: " + id));

        boolean changingApproval = dto.getIsApproved() != null &&
                !Objects.equals(dto.getIsApproved(), specRev.getIsApproved());

        if (changingApproval) {
            Boolean newApproval = dto.getIsApproved();
            specRev.setIsApproved(newApproval);

            specRev.setIsNameApproved(newApproval);
            specRev.setIsDescApproved(newApproval);
            specRev.setIsObsApproved(newApproval);

            if (dto.getApprovalType() == ApprovalEnum.CASCADE) {
                approveLocais(newApproval, specRev.getLocalRevIds());
                approveMateriais(newApproval, specRev.getMaterialRevIds());
            }
        }

        if (dto.getIsNameApproved() != null)
            specRev.setIsNameApproved(dto.getIsNameApproved());

        if (dto.getIsDescApproved() != null)
            specRev.setIsDescApproved(dto.getIsDescApproved());

        if (dto.getIsObsApproved() != null)
            specRev.setIsObsApproved(dto.getIsObsApproved());

        if (dto.getComment() != null && !dto.getComment().isBlank())
            specRev.setComment(dto.getComment());

        EspecificacaoDoc specDoc = especificacaoDocRepository.findById(specRev.getRevisedDocId())
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado: " + specRev.getRevisedDocId()));

        return ResSpecRevDTO.fromDoc(
                especificacaoRevDocElementRepository.save(specRev),
                editorMapper.toResponse(specDoc)
        );
    }

    private void approveLocais(Boolean isApproved, List<ObjectId> localRevIds) {
        Update updateLocal = new Update().set("isApproved", isApproved);
        documentSearch.bulkUpdate(localRevIds, updateLocal, LocalRevDocElement.class);

        List<LocalRevDocElement> locals = documentSearch.findAllByIds(localRevIds, LocalRevDocElement.class);

        List<ObjectId> ambienteIds = locals.stream()
                .flatMap(l -> l.getAmbienteRevs().stream())
                .map(AmbienteRevDocElement::getId)
                .toList();

        approveAmbientes(isApproved, ambienteIds);
    }

    private ResLocalRevDTO updateLocal(ObjectId id, EditLocalRevDocDTO dto) {
        LocalRevDocElement localRev = localRevDocElementRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão de Local não encontrada com o id informado: " + id)
                );

        localRev.setIsApproved(dto.getIsApproved());

        if (dto.getApprovalType().equals(ApprovalEnum.CASCADE)) {
            approveAmbientes(localRev.getIsApproved(), localRev.getAmbienteRevIds());
        }

        if (dto.getComment() != null && !dto.getComment().isEmpty()) {
            localRev.setComment(dto.getComment());
        }

        return ResLocalRevDTO.fromDoc(localRevDocElementRepository.save(localRev));
    }

    private void approveAmbientes(Boolean isApproved, List<ObjectId> ambienteRevIds) {
        Update updateAmb = new Update().set("isApproved", isApproved);
        documentSearch.bulkUpdate(ambienteRevIds, updateAmb, AmbienteRevDocElement.class);

        List<AmbienteRevDocElement> ambientes = documentSearch.findAllByIds(ambienteRevIds, AmbienteRevDocElement.class);

        List<ObjectId> itemIds = ambientes.stream()
                .flatMap(a -> a.getItemRevIds().stream())
                .toList();

        Update updateItems = new Update()
                .set("isApproved", isApproved)
                .set("isDescApproved", isApproved)
                .set("isTypeApproved", isApproved);

        documentSearch.bulkUpdate(itemIds, updateItems, ItemRevDocElement.class);
    }

    private ResAmbRevDTO updateAmbiente(ObjectId id, EditAmbRevDocDTO dto) {
        AmbienteRevDocElement ambienteRev = ambienteRevDocElementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisão de Ambiente não encontrada: " + id));

        if (!Objects.equals(dto.getIsApproved(), ambienteRev.getIsApproved())) {
            ambienteRev.setIsApproved(dto.getIsApproved());

            if (dto.getApprovalType() == ApprovalEnum.CASCADE)
                approveItems(dto.getIsApproved(), ambienteRev.getItemRevIds());
        }

        if (dto.getComment() != null && !dto.getComment().isBlank())
            ambienteRev.setComment(dto.getComment());

        return ResAmbRevDTO.fromDoc(ambienteRevDocElementRepository.save(ambienteRev));
    }

    private void approveItems(Boolean isApproved, List<ObjectId> itemRevIds) {
        Update update = new Update()
                .set("isApproved", isApproved)
                .set("isDescApproved", isApproved)
                .set("isTypeApproved", isApproved);
        documentSearch.bulkUpdate(itemRevIds, update, ItemRevDocElement.class);
    }

    private ResItemRevDTO updateItem(ObjectId id, EditItemRevDocDTO dto) {

        ItemRevDocElement itemRev = itemRevDocElementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revisão de Item não encontrada: " + id));

        boolean changingApproval = dto.getIsApproved() != null &&
                !Objects.equals(dto.getIsApproved(), itemRev.getIsApproved());

        if (changingApproval) {
            Boolean newApproval = dto.getIsApproved();
            itemRev.setIsApproved(newApproval);

            itemRev.setIsDescApproved(newApproval);
            itemRev.setIsTypeApproved(newApproval);
        }

        if (dto.getIsDescApproved() != null)
            itemRev.setIsDescApproved(dto.getIsDescApproved());

        if (dto.getIsTypeApproved() != null)
            itemRev.setIsTypeApproved(dto.getIsTypeApproved());

        if (dto.getComment() != null && !dto.getComment().isBlank())
            itemRev.setComment(dto.getComment());

        return ResItemRevDTO.fromDoc(itemRevDocElementRepository.save(itemRev));
    }

    private void approveMateriais(Boolean isApproved, List<ObjectId> materialRevIds) {
        List<MaterialRevDocElement> materialRevDocs = documentSearch.findAllByIds(materialRevIds, MaterialRevDocElement.class);

        Update update = new Update().set("isApproved", isApproved);
        documentSearch.bulkUpdate(materialRevIds, update, MaterialRevDocElement.class);

        List<ObjectId> marcaRevIds = materialRevDocs.stream()
                .flatMap(mat -> mat.getMarcaRevs().stream())
                .map(MarcaRevDocElement::getId)
                .toList();
        documentSearch.bulkUpdate(marcaRevIds, update, MarcaRevDocElement.class);
    }

    private ResMatRevDTO updateMaterial(ObjectId id, EditMatRevDocDTO dto) {
        MaterialRevDocElement materialRev = materialRevDocElementRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão de Material não encontrada com o id informado: " + id)
                );

        if (dto.getIsApproved() != materialRev.getIsApproved()) {
            materialRev.setIsApproved(dto.getIsApproved());

            if (dto.getApprovalType().equals(ApprovalEnum.CASCADE)) {
                approveMarcas(materialRev.getIsApproved(), materialRev.getMarcaRevIds());
            }
        }

        if (dto.getComment() != null && !dto.getComment().isEmpty()) {
            materialRev.setComment(dto.getComment());
        }

        return ResMatRevDTO.fromDoc(materialRevDocElementRepository.save(materialRev));
    }

    private void approveMarcas(Boolean isApproved, List<ObjectId> marcaRevIds) {
        Update update = new Update().set("isApproved", isApproved);
        documentSearch.bulkUpdate(marcaRevIds, update, MarcaRevDocElement.class);
    }

    private ResMarRevDTO updateMarca(ObjectId id, EditMarRevDocDTO dto) {
        MarcaRevDocElement marcaRev = marcaRevDocElementRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão de Marca não encontrada com o id informado: " + id)
                );

        if (dto.getIsApproved() != marcaRev.getIsApproved()) {
            marcaRev.setIsApproved(dto.getIsApproved());
        }

        if (dto.getComment() != null && !dto.getComment().isEmpty()) {
            marcaRev.setComment(dto.getComment());
        }

        return ResMarRevDTO.fromDoc(marcaRevDocElementRepository.save(marcaRev));
    }


    private ProcessoHistorico createProcessFromOrigin(ProcessoHistorico origin, ProcActionEnum action) {
        ProcessoHistorico newProcess = new ProcessoHistorico();
        newProcess.setEmp(origin.getEmp());
        newProcess.setRevision(origin.getRevision());
        newProcess.setProcAction(action);
        newProcess.setOrigin(origin);
        return newProcess;
    }

    private void finishProcess(ProcessoHistorico proc) {
        proc.setFinished(Instant.now());
        processoHistoricoRepository.save(proc);
    }

    @Transactional
    public void reject(Long id) {
        Revisao rev = findByRevisaoById(id);

        if (!rev.getStatus().equals(RevisaoStatusEnum.INICIADA)) {
            throw new InvalidStageException("Apenas Revisões Iniciadas podem ser Rejeitadas.");
        }

        rev.setStatus(RevisaoStatusEnum.REJEITADA);

        ProcessoHistorico previousProcess = findPreviousProcess(rev, ProcActionEnum.REQUEST);
        finishProcess(previousProcess);
        ProcessoHistorico process = createProcessFromOrigin(previousProcess, ProcActionEnum.REJECTION);
        processoHistoricoRepository.save(process);

        Empreendimento emp = process.getEmp();
        emp.setStatus(EmpStatusEnum.ELABORACAO);
        empreendimentoRepository.save(emp);

        revisaoRepository.save(rev);
    }

    // TODO: Implementar versionamento após aprovação de documento
    @Transactional
    public void approve(Long id) {
        Revisao rev = findByRevisaoById(id);

        Arrays.stream(RevDocElementEnum.values()).forEach(docType -> {
            RevDocSearchParamsDTO params = new RevDocSearchParamsDTO();
            params.setDocType(docType);
            params.setRevisionId(rev.getId());

            revisionService.searchDocs(LoadRevDocParamsDTO.allFalse(), params, true).stream()
                    .findAny().ifPresent(revDoc -> {
                        if (revDoc.getIsApproved() == null) {
                            throw new PendingEvaluationException("Documento do tipo " + docType + " com avaliação pendente: " + revDoc.getId());
                        }
                    });
        });

        ProcessoHistorico previousProcess = findPreviousProcess(rev, ProcActionEnum.REQUEST);
        finishProcess(previousProcess);
        ProcessoHistorico process = createProcessFromOrigin(previousProcess, ProcActionEnum.APPROVAL);
        // TODO: Criar documento pós aprovação e referenciar o ID
        processoHistoricoRepository.save(process);

        Empreendimento emp = process.getEmp();
        emp.setStatus(EmpStatusEnum.FINALIZADO);
        empreendimentoRepository.save(emp);

        rev.setStatus(RevisaoStatusEnum.APROVADA);
        revisaoRepository.save(rev);
    }
}