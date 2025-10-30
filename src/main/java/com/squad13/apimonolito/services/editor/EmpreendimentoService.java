package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.DTO.editor.EmpDTO;
import com.squad13.apimonolito.DTO.editor.EspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.edit.EditEmpDTO;
import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
import com.squad13.apimonolito.DTO.editor.res.ResSpecDTO;
import com.squad13.apimonolito.DTO.revision.ToRevisionDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResSpecRevDTO;
import com.squad13.apimonolito.exceptions.*;
import com.squad13.apimonolito.models.catalog.Padrao;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.models.user.associative.UsuarioEmpreendimento;
import com.squad13.apimonolito.mongo.revision.EspecificacaoRevDocElementRepository;
import com.squad13.apimonolito.repository.catalog.PadraoRepository;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.repository.revision.RevisaoRepository;
import com.squad13.apimonolito.repository.user.UsuarioRepository;
import com.squad13.apimonolito.repository.user.associative.UsuarioEmpreendimentoRepository;
import com.squad13.apimonolito.util.enums.AccessEnum;
import com.squad13.apimonolito.util.enums.EmpStatusEnum;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;
import com.squad13.apimonolito.util.enums.rule.RevisionRule;
import com.squad13.apimonolito.util.mapper.EditorMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpreendimentoService {

    @PersistenceContext
    private EntityManager em;

    private final EmpreendimentoRepository empRepository;

    private final EspecificacaoRevDocElementRepository specRevDocRepository;

    private final UsuarioRepository userRepository;
    private final PadraoRepository padraoRepository;
    private final RevisaoRepository revisaoRepository;
    private final UsuarioEmpreendimentoRepository userEmpRepository;

    private final EspecificacaoService specService;

    private final EditorMapper editorMapper;

    private ResEmpDTO mappingHelper(Empreendimento emp, LoadDocumentParamsDTO params) {
        ResSpecDTO doc = params.isLoadEspecificacao()
                ? specService.findByEmpId(emp.getId(), params)
                : null;

        ResRevDTO rev = null;
        if (params.isLoadRevision()) {
            rev = revisaoRepository.findByEmpreendimento(emp)
                    .map(r -> {
                        List<ResSpecRevDTO> resSpecRevs = specRevDocRepository.findByRevisionId(r.getId()).stream()
                                .map(specRev -> ResSpecRevDTO.fromDoc(specRev, editorMapper.toResponse(specRev.getRevisedDoc())))
                                .toList();

                        return ResRevDTO.from(r, resSpecRevs, null);
                    })
                    .orElse(null);
        }

        return editorMapper.toResponse(emp, doc, rev, params);
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

    public List<ResEmpDTO> findByAttribute(String attribute, String value, LoadDocumentParamsDTO params) {
        boolean attributeExists = Arrays.stream(Empreendimento.class.getDeclaredFields())
                .anyMatch(f -> f.getName().equals(attribute));

        if (!attributeExists && !attribute.equals("padrao") && !attribute.equals("usuarioList")) {
            throw new InvalidAttributeException("Atributo inválido: " + attribute);
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Empreendimento> cq = cb.createQuery(Empreendimento.class);
        Root<Empreendimento> root = cq.from(Empreendimento.class);

        Predicate predicate;

        if (attribute.equals("padrao")) {
            Join<Empreendimento, Padrao> padraoJoin = root.join("padrao", JoinType.LEFT);
            predicate = (value == null)
                    ? cb.isNull(padraoJoin.get("name"))
                    : cb.like(cb.lower(padraoJoin.get("name")), "%" + value.toLowerCase() + "%");
        } else if (attribute.equals("usuarioList")) {
            Join<Empreendimento, UsuarioEmpreendimento> relJoin = root.join("usuarioList", JoinType.LEFT);
            Join<UsuarioEmpreendimento, Usuario> userJoin = relJoin.join("usuario", JoinType.LEFT);
            predicate = (value == null)
                    ? cb.isNull(userJoin.get("id"))
                    : cb.equal(userJoin.get("id").as(String.class), value);
        } else {
            if (value == null) {
                predicate = cb.isNull(root.get(attribute));
            } else {
                Class<?> fieldType = root.get(attribute).getJavaType();

                if (fieldType.isEnum() && fieldType.equals(EmpStatusEnum.class)) {
                    EmpStatusEnum status = EmpStatusEnum.fromString(value);
                    predicate = cb.equal(root.get(attribute), status);
                } else if (Number.class.isAssignableFrom(fieldType)) {
                    predicate = cb.equal(root.get(attribute).as(String.class), value);
                } else {
                    predicate = cb.like(cb.lower(root.get(attribute).as(String.class)), "%" + value.toLowerCase() + "%");
                }
            }
        }

        cq.select(root).where(predicate);

        List<Empreendimento> results = em.createQuery(cq).getResultList();

        return results.stream()
                .map(emp -> mappingHelper(emp, params))
                .toList();
    }

    private void blockEmp(Empreendimento emp) {
        if (emp.getStatus().equals(EmpStatusEnum.EM_REVISAO)) {
            throw new InvalidStageException("O Empreendimento " + emp.getName() + " já está em Revisão");
        }

        emp.setStatus(EmpStatusEnum.EM_REVISAO);
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

    // TODO: implementar uma lógica de continuação de Revisão
    public ResRevDTO sendToRevision(Long id, ToRevisionDTO dto) {
        Empreendimento emp = findByIdOrThrow(id);

        blockEmp(emp);
        createUserAccess(emp, dto.revisorId(), AccessEnum.REVISOR);

        Revisao rev = new Revisao();
        rev.setEmpreendimento(emp);
        rev.setStatus(RevisaoStatusEnum.PENDENTE);
        rev.setRule(RevisionRule.START_BY_ASSIGNED);

        ResEmpDTO resEmpDTO = mappingHelper(emp, LoadDocumentParamsDTO.allFalse());
        return ResRevDTO.from(revisaoRepository.save(rev), null, resEmpDTO);
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
        emp.setStatus(EmpStatusEnum.EM_ELABORACAO);

        if (dto.padraoId() != null) {
            Padrao padrao = padraoRepository.findById(dto.padraoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Padrão não encontrado com o ID: " + dto.padraoId()
                    ));
            emp.setPadrao(padrao);
        }

        Empreendimento saved = empRepository.save(emp);
        createUserAccess(emp, dto.creatorId(), AccessEnum.CRIADOR);

        EspecificacaoDocDTO specDocDTO = new EspecificacaoDocDTO(
                emp.getName(),
                null,
                null,
                emp.getId(),
                dto.init()
        );
        specService.create(specDocDTO);

        return mappingHelper(saved, loadDTO);
    }

    private void ensureUniqueName(Empreendimento emp, EditEmpDTO dto) {
        empRepository.findByName(dto.name())
                .ifPresent(emp1 -> {
                    if (Objects.equals(emp.getId(), emp1.getId()))
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
        } else {
            emp.setPadrao(null);
        }

        Empreendimento updated = empRepository.save(emp);
        return mappingHelper(updated, loadDTO);
    }

    public void delete(Long id) {
        Empreendimento emp = findByIdOrThrow(id);
        specService.delete(emp.getId());
        revisaoRepository.deleteByEmpreendimento_Id(id);
        userEmpRepository.deleteByEmpreendimento_Id(id);

        empRepository.delete(emp);
    }

    public ResEmpDTO deactivate(Long id, LoadDocumentParamsDTO loadDTO) {
        Empreendimento existing = findByIdOrThrow(id);
        existing.setStatus(EmpStatusEnum.CANCELADO);
        return mappingHelper(existing, LoadDocumentParamsDTO.allTrue());
    }
}