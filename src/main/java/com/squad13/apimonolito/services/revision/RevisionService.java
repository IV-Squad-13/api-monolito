package com.squad13.apimonolito.services.revision;

import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
import com.squad13.apimonolito.DTO.editor.res.ResSpecDTO;
import com.squad13.apimonolito.DTO.revision.LoadRevDocParamsDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResSpecRevDTO;
import com.squad13.apimonolito.exceptions.InvalidStageException;
import com.squad13.apimonolito.exceptions.InvalidUserException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.revision.mongo.*;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.models.user.associative.UsuarioEmpreendimento;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.revision.EspecificacaoRevDocElementRepository;
import com.squad13.apimonolito.repository.revision.RevisaoRepository;
import com.squad13.apimonolito.repository.user.UsuarioRepository;
import com.squad13.apimonolito.repository.user.associative.UsuarioEmpreendimentoRepository;
import com.squad13.apimonolito.services.editor.EmpreendimentoService;
import com.squad13.apimonolito.util.builder.DocElementBuilder;
import com.squad13.apimonolito.util.enums.AccessEnum;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;
import com.squad13.apimonolito.util.enums.rule.RevisionRule;
import com.squad13.apimonolito.util.mapper.EditorMapper;
import com.squad13.apimonolito.util.mapper.RevisionMapper;
import com.squad13.apimonolito.util.search.DocumentSearch;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RevisionService {

    private final EmpreendimentoService empService;

    private final RevisaoRepository revRepository;

    private final EspecificacaoRevDocElementRepository revDocRepository;

    private final UsuarioRepository userRepository;
    private final UsuarioEmpreendimentoRepository userEmpRepository;
    private final DocumentSearch documentSearch;
    private final RevisionMapper revisionMapper;
    private final EditorMapper editorMapper;
    private final DocElementBuilder docBuilder;
    private final EspecificacaoDocRepository specDocRespository;

    private ResRevDTO mappingHelper(Revisao rev, LoadRevDocParamsDTO params) {
        List<ResSpecRevDTO> revDocs = Collections.emptyList();
        ResEmpDTO emp = null;

        if (params.isLoadRevDocuments()) {
            List<EspecificacaoRevDocElement> revDocElements = revDocRepository.findByRevisionId(rev.getId());
            revDocElements.forEach(revDoc -> {
                EspecificacaoDoc specDoc = specDocRespository.findById(revDoc.getRevisedDocId())
                                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o ID: " + revDoc.getRevisedDocId()));
                revDoc.setRevisedDoc(specDoc);
            });

            revDocs = revDocElements.stream()
                    .map(specRev -> ResSpecRevDTO.fromDoc(specRev, editorMapper.toResponse(specRev.getRevisedDoc())))
                    .toList();
        }

        if (params.isLoadEmpreendimento()) {
            emp = empService.findById(rev.getEmpreendimento().getId(), LoadDocumentParamsDTO.allFalse());
        }

        return ResRevDTO.from(rev, revDocs, emp);
    }

    private Revisao findByIdOrThrow(Long id) {
        return revRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão não encontrada: " + id)
                );
    }

    private Usuario findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Usuário não encontrado: " + id)
                );
    }

    private UsuarioEmpreendimento findUserAccessOrThrow(Long userId, Long empId, AccessEnum access) {
        return userEmpRepository.findByUsuario_IdAndEmpreendimento_IdAndAccessLevel(userId, empId, access)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Nenhum Usuário com ID: " + userId + " não encontrado no Empreendimento " + empId)
                );
    }

    private ResSpecDTO getSpecByEmpId(Long id) {
        Aggregation aggregation = docBuilder.buildAggregation(LoadDocumentParamsDTO.allTrue());
        return documentSearch.findWithAggregation("especificacoes", ResSpecDTO.class, aggregation)
                .stream()
                .filter(spec -> Objects.equals(spec.getEmpreendimentoId(), id))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Especificação não encontrada para o empreendimento ID: " + id));
    }

    public List<ResRevDTO> findAll(LoadRevDocParamsDTO params) {
        return revRepository.findAll().stream()
                .map(rev -> mappingHelper(rev, params))
                .toList();
    }

    public ResRevDTO findById(Long id, LoadRevDocParamsDTO params) {
        return revRepository.findById(id)
                .map(rev -> mappingHelper(rev, params))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão não encontrada: " + id)
                );
    }

    public ResRevDTO findByEmpreendimentoId(Long id, LoadRevDocParamsDTO params) {
        return revRepository.findByEmpreendimento_Id(id)
                .map(rev -> mappingHelper(rev, params))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão não encontrada para o Empreendimento: " + id)
                );
    }

    @Transactional
    public ResRevDTO start(Long revisionId, Long revisorId, LoadRevDocParamsDTO params) {
        Revisao revision = findByIdOrThrow(revisionId);
        validateRevisionStart(revision);

        Usuario revisor = findUserOrThrow(revisorId);
        validateUserAccess(revisor, revision);

        revision.setStatus(RevisaoStatusEnum.INICIADA);
        revRepository.save(revision);

        ResSpecDTO spec = getSpecByEmpId(revision.getEmpreendimento().getId());

        EspecificacaoRevDocElement specRev = new EspecificacaoRevDocElement();
        specRev.setRevisionId(revision.getId());
        specRev.setRevisedDocId(new ObjectId(spec.getId()));

        generateFullRevision(specRev, spec);

        return mappingHelper(revision, params);
    }

    private void validateRevisionStart(Revisao revision) {
        if (revision.getStatus() != RevisaoStatusEnum.PENDENTE) {
            throw new InvalidStageException(
                    "A revisão não pode ser iniciada enquanto estiver no estágio: " + revision.getStatus()
            );
        }
    }

    private void validateUserAccess(Usuario user, Revisao revision) {
        UsuarioEmpreendimento access = findUserAccessOrThrow(
                user.getId(),
                revision.getEmpreendimento().getId(),
                AccessEnum.REVISOR
        );

        boolean unauthorized =
                (revision.getRule() == RevisionRule.START_BY_ASSIGNED && !access.getUsuario().equals(user))
                        || access.getAccessLevel().notAllowed(user.getPapel().getNome());

        if (unauthorized) {
            throw new InvalidUserException(
                    "O usuário '" + user.getNome() + "' não pode iniciar a Revisão de ID: " + revision.getId()
            );
        }
    }

    private ObjectId newId() {
        return new ObjectId();
    }

    private void generateFullRevision(EspecificacaoRevDocElement specRev, ResSpecDTO specDTO) {
        List<LocalRevDocElement> localRevs = generateLocalRevs(specRev.getRevisionId(), specDTO);
        documentSearch.bulkSave(LocalRevDocElement.class, localRevs);

        List<MaterialRevDocElement> materialRevs = generateMaterialRevs(specRev.getRevisionId(), specDTO);
        documentSearch.bulkSave(MaterialRevDocElement.class, materialRevs);

        specRev.setLocalRevs(localRevs);
        specRev.setLocalRevIds(extractIds(localRevs));
        specRev.setMaterialRevs(materialRevs);
        specRev.setMaterialRevIds(extractIds(materialRevs));

        revDocRepository.save(specRev);
    }

    private List<LocalRevDocElement> generateLocalRevs(Long revisionId, ResSpecDTO specDTO) {
        return safeList(specDTO.getLocais())
                .stream()
                .map(resLocal -> {
                    LocalRevDocElement localRev = new LocalRevDocElement();
                    localRev.setId(newId());
                    localRev.setRevisedDocId(new ObjectId(resLocal.getId()));
                    localRev.setRevisionId(revisionId);
                    localRev.setIsApproved(null);
                    localRev.setComment(null);

                    List<AmbienteRevDocElement> ambienteRevs = safeList(resLocal.getAmbientes())
                            .stream()
                            .map(resAmb -> {
                                AmbienteRevDocElement ambRev =
                                        revisionMapper.toRevDoc(resAmb, revisionId, AmbienteRevDocElement::new);
                                ambRev.setId(newId());

                                List<ItemRevDocElement> itemRevs = safeList(resAmb.getItems())
                                        .stream()
                                        .map(resItem -> {
                                            ItemRevDocElement itemRev =
                                                    revisionMapper.toRevDoc(resItem, revisionId, ItemRevDocElement::new);
                                            itemRev.setId(newId());
                                            itemRev.setIsDescApproved(null);
                                            itemRev.setIsTypeApproved(null);
                                            return itemRev;
                                        })
                                        .toList();

                                if (!itemRevs.isEmpty()) {
                                    documentSearch.bulkSave(ItemRevDocElement.class, itemRevs);
                                }

                                ambRev.setItemRevs(itemRevs);
                                ambRev.setItemRevIds(extractIds(itemRevs));
                                return ambRev;
                            })
                            .toList();

                    if (!ambienteRevs.isEmpty()) {
                        documentSearch.bulkSave(AmbienteRevDocElement.class, ambienteRevs);
                    }

                    localRev.setAmbienteRevs(ambienteRevs);
                    localRev.setAmbienteRevIds(extractIds(ambienteRevs));
                    return localRev;
                })
                .toList();
    }

    private List<MaterialRevDocElement> generateMaterialRevs(Long revisionId, ResSpecDTO specDTO) {
        return safeList(specDTO.getMateriais())
                .stream()
                .map(resMat -> {
                    MaterialRevDocElement materialRev =
                            revisionMapper.toRevDoc(resMat, revisionId, MaterialRevDocElement::new);
                    materialRev.setId(newId());

                    List<MarcaRevDocElement> marcaRevs = safeList(resMat.getMarcas())
                            .stream()
                            .map(resMarca -> {
                                MarcaRevDocElement marcaRev = revisionMapper.toRevDoc(resMarca, revisionId, MarcaRevDocElement::new);
                                marcaRev.setId(newId());
                                return marcaRev;
                            })
                            .toList();

                    if (!marcaRevs.isEmpty()) {
                        documentSearch.bulkSave(MarcaRevDocElement.class, marcaRevs);
                    }

                    materialRev.setMarcaRevs(marcaRevs);
                    materialRev.setMarcaRevIds(extractIds(marcaRevs));
                    return materialRev;
                })
                .toList();
    }

    private <T extends RevDocElement> List<ObjectId> extractIds(List<T> elements) {
        return safeList(elements).stream()
                .map(RevDocElement::getId)
                .filter(Objects::nonNull)
                .toList();
    }

    private <T> List<T> safeList(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }

    public void delete(Long id) {
        Revisao rev = findByIdOrThrow(id);
        revRepository.delete(rev);
    }
}