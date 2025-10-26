package com.squad13.apimonolito.services.revision;

import com.squad13.apimonolito.DTO.editor.LoadDocumentParamsDTO;
import com.squad13.apimonolito.DTO.editor.res.ResEmpDTO;
import com.squad13.apimonolito.DTO.revision.LoadRevDocParamsDTO;
import com.squad13.apimonolito.DTO.revision.StartRevDTO;
import com.squad13.apimonolito.DTO.revision.res.ResRevDTO;
import com.squad13.apimonolito.exceptions.InvalidStageException;
import com.squad13.apimonolito.exceptions.InvalidUserException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.revision.mongo.EspecificacaoRevDocElement;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.models.user.Usuario;
import com.squad13.apimonolito.models.user.associative.UsuarioEmpreendimento;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.revision.EspecificacaoRevDocElementRepository;
import com.squad13.apimonolito.repository.revision.RevisaoRepository;
import com.squad13.apimonolito.repository.user.UsuarioRepository;
import com.squad13.apimonolito.repository.user.associative.UsuarioEmpreendimentoRepository;
import com.squad13.apimonolito.services.editor.EmpreendimentoService;
import com.squad13.apimonolito.util.enums.AccessEnum;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;
import com.squad13.apimonolito.util.enums.rule.RevisionRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RevisionService {

    private final EmpreendimentoService empService;

    private final RevisaoRepository revRepository;

    private final EspecificacaoDocRepository specRepository;
    private final EspecificacaoRevDocElementRepository revDocRepository;

    private final UsuarioRepository userRepository;
    private final UsuarioEmpreendimentoRepository userEmpRepository;

    private ResRevDTO mappingHelper(Revisao rev, LoadRevDocParamsDTO params) {
        List<EspecificacaoRevDocElement> revDocs = params.isLoadRevDocuments()
                ? revDocRepository.findByRevisionId(rev.getId())
                : null;

        ResEmpDTO emp = params.isLoadEmpreendimento()
                ? empService.findById(rev.getEmpreendimento().getId(), LoadDocumentParamsDTO.allFalse())
                : null;

        return ResRevDTO.toDTO(rev, revDocs, emp);
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

    public List<ResRevDTO> findALl(LoadRevDocParamsDTO params) {
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

    public ResRevDTO start(Long id, Long revisorId, LoadRevDocParamsDTO params) {
        Revisao rev = findByIdOrThrow(id);

        if (!rev.getStatus().equals(RevisaoStatusEnum.PENDENTE)) {
            throw new InvalidStageException("A Revisão não pode ser iniciada enquanto estiver no estágio: " + rev.getStatus());
        }

        Usuario user = findUserOrThrow(revisorId);
        UsuarioEmpreendimento userAccess = findUserAccessOrThrow(user.getId(), rev.getEmpreendimento().getId(), AccessEnum.REVISOR);

        if ((rev.getRule().equals(RevisionRule.START_BY_ASSIGNED)
                && !userAccess.getUsuario().equals(user))
                // Talvez esse último não faça sentido
                || userAccess.getAccessLevel().notAllowed(user.getPapel().getNome())) {
            throw new InvalidUserException("O usuário '" + user.getNome() + "' não pode iniciar a Revisão de ID: " + rev.getId());
        }

        rev.setStatus(RevisaoStatusEnum.INICIADA);

        EspecificacaoDoc spec = specRepository.findByEmpreendimentoId(rev.getEmpreendimento().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o Empreendimento: " + rev.getEmpreendimento().getId()));

        EspecificacaoRevDocElement revDoc = new EspecificacaoRevDocElement();
        revDoc.setRevisionId(rev.getId());
        revDoc.setEspecificacaoDocId(spec.getId());
        revDocRepository.save(revDoc);

        return mappingHelper(
                revRepository.save(rev),
                params
        );
    }

    public void delete(Long id) {
        Revisao rev = findByIdOrThrow(id);
        revRepository.delete(rev);
    }
}