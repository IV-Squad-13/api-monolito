package com.squad13.apimonolito.services.editor;


import com.squad13.apimonolito.DTO.editor.EmpDTO;
import com.squad13.apimonolito.DTO.editor.EmpSearchParamsDTO;
import com.squad13.apimonolito.DTO.editor.EspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.edit.EditEmpDTO;
import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
import com.squad13.apimonolito.DTO.editor.res.ResSpecDTO;
import com.squad13.apimonolito.DTO.revision.ToRevisionDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResSpecRevDTO;
import com.squad13.apimonolito.exceptions.exceptions.InvalidStageException;
import com.squad13.apimonolito.exceptions.exceptions.InvalidUserException;
import com.squad13.apimonolito.exceptions.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.revision.relational.ProcessoHistorico;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.models.user.associative.UsuarioEmpreendimento;
import com.squad13.apimonolito.mongo.revision.EspecificacaoRevDocElementRepository;
import com.squad13.apimonolito.repository.catalog.PadraoRepository;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.repository.revision.ProcessoHistoricoRepository;
import com.squad13.apimonolito.repository.revision.RevisaoRepository;
import com.squad13.apimonolito.repository.user.UsuarioRepository;
import com.squad13.apimonolito.repository.user.associative.UsuarioEmpreendimentoRepository;
import com.squad13.apimonolito.util.enums.*;
import com.squad13.apimonolito.util.enums.rule.RevisionRule;
import com.squad13.apimonolito.util.mapper.EditorMapper;
import com.squad13.apimonolito.util.search.CatalogSearch;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpreendimentoService {

    private final ProcessoHistoricoRepository processoHistoricoRepository;
    private final CatalogSearch catalogSearch;

    private final EmpreendimentoRepository empRepository;

    private final EspecificacaoRevDocElementRepository specRevDocRepository;

    private final UsuarioRepository userRepository;
    private final PadraoRepository padraoRepository;
    private final RevisaoRepository revisaoRepository;
    private final UsuarioEmpreendimentoRepository userEmpRepository;

    private final EspecificacaoService specService;

    private final EditorMapper editorMapper;

    private ResEmpDTO mappingHelper(Empreendimento emp, LoadDocumentParamsDTO params) {
        ResSpecDTO doc = params.isLoadEspecificacao() || params.isLoadAll()
                ? specService.findByEmpId(emp.getId(), params)
                : null;

        ResSpecDTO refDoc = emp.getInit().equals(DocInitializationEnum.IMPORT) && params.isLoadAll()
                ? specService.findById(emp.getReferenceDocId(), LoadDocumentParamsDTO.allFalse())
                : null;

        ResRevDTO rev = null;
        if (params.isLoadRevision() || params.isLoadAll()) {
            rev = processoHistoricoRepository.findByEmp_Id(emp.getId()).stream()
                    .map(ProcessoHistorico::getRevision)
                    .map(r -> {
                        List<ResSpecRevDTO> resSpecRevs = specRevDocRepository.findByRevisionId(r.getId()).stream()
                                .map(specRev -> {
                                    ResSpecDTO specDTO = specRev.getRevisedDoc() != null
                                            ? editorMapper.toResponse(specRev.getRevisedDoc())
                                            : specService.findById(specRev.getRevisedDocId(), LoadDocumentParamsDTO.allFalse());
                                    return ResSpecRevDTO.fromDoc(specRev, specDTO);
                                }).toList();

                        return ResRevDTO.from(r, resSpecRevs, null);
                    })
                    .findFirst()
                    .orElse(null);
        }

        return editorMapper.toResponse(emp, doc, refDoc, rev, params);
    }

    private Usuario findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado com o ID: " + id
                ));
    }

    public List<ResEmpDTO> findAll(LoadDocumentParamsDTO params) {
        return empRepository.findAll()
                .stream()
                .map(emp -> mappingHelper(emp, params))
                .toList();
    }

    private Empreendimento findByIdOrThrow(Long id) {
        return empRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empreendimento com ID: " + id + " não encontrado."));
    }

    public ResEmpDTO findById(Long id, LoadDocumentParamsDTO params) {
        return empRepository.findById(id)
                .map(emp -> mappingHelper(emp, params))
                .orElseThrow(() -> new ResourceNotFoundException("Empreendimento com ID: " + id + " não encontrado."));
    }

    public List<ResEmpDTO> search(EmpSearchParamsDTO searchParams, LoadDocumentParamsDTO loadParams) {
        Map<String, Object> typedFilters = EmpSearchParamsDTO.buildFilters(searchParams);

        List<Empreendimento> emps = catalogSearch.findByCriteria(typedFilters, Empreendimento.class);
        return emps.stream()
                .map(emp -> mappingHelper(emp, loadParams))
                .toList();
    }

    private void blockEmp(Empreendimento emp) {
        if (emp.getStatus().equals(EmpStatusEnum.SUSPENSO)) {
            throw new InvalidStageException("O Empreendimento " + emp.getName() + " já está em Revisão");
        }

        emp.setStatus(EmpStatusEnum.SUSPENSO);
        empRepository.save(emp);
    }

    private void createUserAccess(Empreendimento emp, Long userId, AccessEnum accessType) {
        Usuario user = findUser(userId);

        if (accessType.notAllowed(user.getPapel().getNome()))
            throw new InvalidUserException("O usuário '" + user.getNome() + "' não pode exercer a função de " + accessType);

        UsuarioEmpreendimento userEmp = new UsuarioEmpreendimento();
        userEmp.setUsuario(user);
        userEmp.setEmpreendimento(emp);
        userEmp.setAccessLevel(accessType);
        userEmpRepository.save(userEmp);
    }

    private Revisao createRevision() {
        Revisao rev = new Revisao();
        rev.setStatus(RevisaoStatusEnum.PENDENTE);
        rev.setRule(RevisionRule.START_BY_ASSIGNED);
        return rev;
    }

    private ProcessoHistorico createProcessoHistorico(Empreendimento emp, Revisao rev, ProcActionEnum procAction) {
        ProcessoHistorico proc = new ProcessoHistorico();
        proc.setEmp(emp);
        proc.setRevision(rev);
        proc.setProcAction(procAction);

        return proc;
    }

    private void finishProcess(ProcessoHistorico proc) {
        proc.setFinished(Instant.now());
        processoHistoricoRepository.save(proc);
    }

    public ResRevDTO requestRevision(Long id, ToRevisionDTO dto) {
        Empreendimento emp = findByIdOrThrow(id);

        List<ProcessoHistorico> processes = processoHistoricoRepository.findByEmp_Id(id);

        Set<Long> finishedRevisionIds = processes.stream()
                .filter(p -> p.getProcAction() == ProcActionEnum.APPROVAL)
                .map(p -> p.getRevision().getId())
                .collect(Collectors.toSet());

        List<ProcessoHistorico> activeProcs = processes.stream()
                .filter(p -> !finishedRevisionIds.contains(p.getRevision().getId()))
                .toList();

        Revisao revision;
        ProcessoHistorico newProcess;
        if (!activeProcs.isEmpty()) {
            ProcessoHistorico rejectedProcess = activeProcs.stream()
                    .filter(p -> p.getProcAction() == ProcActionEnum.REJECTION && p.getFinished() == null)
                    .findFirst()
                    .orElse(null);

            assert rejectedProcess != null;
            revision = rejectedProcess.getRevision();
            revision.setStatus(RevisaoStatusEnum.INICIADA);
            revision = revisaoRepository.save(revision);

            finishProcess(rejectedProcess);

            newProcess = createProcessoHistorico(emp, revision, ProcActionEnum.REQUEST);
            newProcess.setOrigin(rejectedProcess);
        } else {
            revision = createRevision();
            revision = revisaoRepository.save(revision);

            newProcess = createProcessoHistorico(emp, revision, ProcActionEnum.REQUEST);
        }

        processoHistoricoRepository.save(newProcess);

        blockEmp(emp);
        createUserAccess(emp, dto.revisorId(), AccessEnum.REVISOR);

        ResEmpDTO resEmpDTO = mappingHelper(emp, LoadDocumentParamsDTO.allFalse());
        return ResRevDTO.from(revision, null, resEmpDTO);
    }

    public ResEmpDTO create(EmpDTO dto, LoadDocumentParamsDTO loadDTO) {
        empRepository.findByName(dto.name())
                .ifPresent(emp -> {
                    throw new ResourceAlreadyExistsException(
                            "Já existe um empreendimento com o nome: " + dto.name()
                    );
                });

        Empreendimento emp = new Empreendimento();
        emp.setName(dto.name());
        emp.setStatus(EmpStatusEnum.ELABORACAO);
        emp.setInit(dto.init());

        if (dto.padraoId() != null) {
            Padrao padrao = padraoRepository.findById(dto.padraoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Padrão não encontrado com o ID: " + dto.padraoId()
                    ));
            emp.setPadrao(padrao);
        }

        if (dto.docImportId() != null) {
            emp.setReferenceDocId(dto.docImportId());
        }

        Empreendimento saved = empRepository.save(emp);
        createUserAccess(emp, dto.creatorId(), AccessEnum.CRIADOR);

        EspecificacaoDocDTO specDocDTO = new EspecificacaoDocDTO(
                emp.getName(),
                null,
                null,
                emp.getId(),
                dto.empImportId(),
                dto.docImportId(),
                dto.init()
        );

        specService.create(specDocDTO);
        return mappingHelper(saved, loadDTO);
    }

    private void ensureUniqueName(Empreendimento emp, EditEmpDTO dto) {
        empRepository.findByName(dto.name())
                .ifPresent(emp1 -> {
                    if (!Objects.equals(emp.getId(), emp1.getId()))
                        throw new ResourceAlreadyExistsException(
                                "O nome '" + dto.name() + "' já está sendo usado."
                        );
                });
    }

    // TODO: Atualizar usuários associados ao empreendimento
    public ResEmpDTO update(Long id, EditEmpDTO dto, LoadDocumentParamsDTO loadDTO) {
        Empreendimento emp = findByIdOrThrow(id);

        if (dto.name() != null && !dto.name().isBlank()) {
            ensureUniqueName(emp, dto);
            emp.setName(dto.name());
        }

        // TODO: Definir o que acontece se o padrão for mudado
        if (dto.padraoId() != null) {
            Long currentPadraoId = emp.getPadrao() != null ? emp.getPadrao().getId() : null;
            if (!Objects.equals(currentPadraoId, dto.padraoId())) {
                Padrao novoPadrao = padraoRepository.findById(dto.padraoId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Padrão não encontrado com o ID: " + dto.padraoId()
                        ));
                emp.setPadrao(novoPadrao);
            }
        }

        Empreendimento updated = empRepository.save(emp);
        return mappingHelper(updated, loadDTO);
    }

    public void delete(Long id) {
        Empreendimento emp = findByIdOrThrow(id);
        specService.delete(emp.getId());
        processoHistoricoRepository.deleteAllByEmp_Id(id);
        userEmpRepository.deleteByEmpreendimento_Id(id);

        empRepository.delete(emp);
    }

    public ResEmpDTO deactivate(Long id, LoadDocumentParamsDTO loadDTO) {
        Empreendimento existing = findByIdOrThrow(id);
        existing.setStatus(EmpStatusEnum.CANCELADO);
        return mappingHelper(existing, LoadDocumentParamsDTO.allTrue());
    }
}