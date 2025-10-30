package com.squad13.apimonolito.services.revision;

import com.squad13.apimonolito.DTO.revision.edit.*;
import com.squad13.apimonolito.DTO.revision.res.*;
import com.squad13.apimonolito.exceptions.InvalidStageException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.revision.mongo.*;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.revision.*;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.repository.revision.RevisaoRepository;
import com.squad13.apimonolito.util.enums.ApprovalEnum;
import com.squad13.apimonolito.util.enums.EmpStatusEnum;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;
import com.squad13.apimonolito.util.mapper.EditorMapper;
import com.squad13.apimonolito.util.search.DocumentSearch;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final LocalRevDocElementRepository localRevDocElementRepository;
    private final AmbienteRevDocElementRepository ambienteRevDocElementRepository;
    private final ItemRevDocElementRepository itemRevDocElementRepository;
    private final DocumentSearch documentSearch;
    private final MaterialRevDocElementRepository materialRevDocElementRepository;
    private final MarcaRevDocElementRepository marcaRevDocElementRepository;
    private final EspecificacaoRevDocElementRepository especificacaoRevDocElementRepository;
    private final EspecificacaoDocRepository especificacaoDocRepository;
    private final EditorMapper editorMapper;
    private final RevisaoRepository revisaoRepository;
    private final EmpreendimentoRepository empreendimentoRepository;

    private Revisao findByRevisaoById(Long id) {
        return revisaoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão não encontrada: " + id)
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
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão de Especificação não encontrada com o id informado: " + id)
                );

        if (dto.getIsApproved() != specRev.getIsApproved()) {
            specRev.setIsApproved(dto.getIsApproved());

            if (dto.getApprovalType().equals(ApprovalEnum.CASCADE)) {
                approveLocais(specRev.getIsApproved(), specRev.getLocalRevIds());
                approveMateriais(specRev.getIsApproved(), specRev.getMaterialRevIds());
            }
        }

        if (dto.getComment() != null && !dto.getComment().isEmpty()) {
            specRev.setComment(dto.getComment());
        }

        if (dto.getIsNameApproved() != specRev.getIsNameApproved()) {
            specRev.setIsNameApproved(dto.getIsNameApproved());
        }

        if (dto.getIsDescApproved() != specRev.getIsDescApproved()) {
            specRev.setIsDescApproved(dto.getIsDescApproved());
        }

        if (dto.getIsObsApproved() != specRev.getIsObsApproved()) {
            specRev.setIsObsApproved(dto.getIsObsApproved());
        }

        EspecificacaoDoc specDoc = especificacaoDocRepository.findById(specRev.getRevisedDocId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Documento de Especificação não encontrado para: " + specRev.getRevisedDocId())
                );
        return ResSpecRevDTO.fromDoc(especificacaoRevDocElementRepository.save(specRev), editorMapper.toResponse(specDoc));
    }

    private void approveLocais(Boolean isApproved, List<ObjectId> localRevIds) {
        List<LocalRevDocElement> localRevDocs = documentSearch.findAllByIds(localRevIds, LocalRevDocElement.class);

        Update update = new Update().set("isApproved", isApproved);
        documentSearch.updateMany(localRevIds, update, LocalRevDocElement.class);

        List<ObjectId> ambienteRevIds = localRevDocs.stream()
                .flatMap(local -> local.getAmbienteRevs().stream())
                .map(AmbienteRevDocElement::getId)
                .toList();
        approveAmbientes(isApproved, ambienteRevIds);
    }

    private ResLocalRevDTO updateLocal(ObjectId id, EditLocalRevDocDTO dto) {
        LocalRevDocElement localRev = localRevDocElementRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão de Local não encontrada com o id informado: " + id)
                );

        if (dto.getIsApproved() != localRev.getIsApproved()) {
            localRev.setIsApproved(dto.getIsApproved());

            if (dto.getApprovalType().equals(ApprovalEnum.CASCADE)) {
                approveAmbientes(localRev.getIsApproved(), localRev.getAmbienteRevIds());
            }
        }

        if (dto.getComment() != null && !dto.getComment().isEmpty()) {
            localRev.setComment(dto.getComment());
        }

        return ResLocalRevDTO.fromDoc(localRevDocElementRepository.save(localRev));
    }

    private void approveAmbientes(Boolean isApproved, List<ObjectId> ambienteRevIds) {
        List<AmbienteRevDocElement> ambienteRevDocs = documentSearch.findAllByIds(ambienteRevIds, AmbienteRevDocElement.class);

        Update update = new Update().set("isApproved", isApproved);
        documentSearch.updateMany(ambienteRevIds, update, AmbienteRevDocElement.class);

        List<ObjectId> itemRevIds = ambienteRevDocs.stream()
                .flatMap(amb -> amb.getItemRevs().stream())
                .map(ItemRevDocElement::getId)
                .toList();
        documentSearch.updateMany(itemRevIds, update, ItemRevDocElement.class);
    }

    private ResAmbRevDTO updateAmbiente(ObjectId id, EditAmbRevDocDTO dto) {
        AmbienteRevDocElement ambienteRev = ambienteRevDocElementRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão de Ambiente não encontrada com o id informado: " + id)
                );

        if (dto.getIsApproved() != ambienteRev.getIsApproved()) {
            ambienteRev.setIsApproved(dto.getIsApproved());

            if (dto.getApprovalType().equals(ApprovalEnum.CASCADE)) {
                approveItems(ambienteRev.getIsApproved(), ambienteRev.getItemRevIds());
            }
        }

        if (dto.getComment() != null && !dto.getComment().isEmpty()) {
            ambienteRev.setComment(dto.getComment());
        }

        return ResAmbRevDTO.fromDoc(ambienteRevDocElementRepository.save(ambienteRev));
    }

    private void approveItems(Boolean isApproved, List<ObjectId> itemRevIds) {
        Update update = new Update().set("isApproved", isApproved);
        documentSearch.updateMany(itemRevIds, update, ItemRevDocElement.class);
    }

    private ResItemRevDTO updateItem(ObjectId id, EditItemRevDocDTO dto) {
        ItemRevDocElement itemRev = itemRevDocElementRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Revisão de Item não encontrada com o id informado: " + id)
                );

        if (dto.getIsApproved() != itemRev.getIsApproved()) {
            itemRev.setIsApproved(dto.getIsApproved());
        }

        if (dto.getComment() != null && !dto.getComment().isEmpty()) {
            itemRev.setComment(dto.getComment());
        }

        if (dto.getIsDescApproved() != null) {
            itemRev.setIsDescApproved(dto.getIsDescApproved());
        }

        if (dto.getIsTypeApproved() != null) {
            itemRev.setIsTypeApproved(dto.getIsTypeApproved());
        }

        return ResItemRevDTO.fromDoc(itemRevDocElementRepository.save(itemRev));
    }

    private void approveMateriais(Boolean isApproved, List<ObjectId> materialRevIds) {
        List<MaterialRevDocElement> materialRevDocs = documentSearch.findAllByIds(materialRevIds, MaterialRevDocElement.class);

        Update update = new Update().set("isApproved", isApproved);
        documentSearch.updateMany(materialRevIds, update, MaterialRevDocElement.class);

        List<ObjectId> marcaRevIds = materialRevDocs.stream()
                .flatMap(mat -> mat.getMarcaRevs().stream())
                .map(MarcaRevDocElement::getId)
                .toList();
        documentSearch.updateMany(marcaRevIds, update, MarcaRevDocElement.class);
    }

    // TODO: Implementar lógica de continuação da Elaboração
    public void reject(Long id) {
        Revisao rev = findByRevisaoById(id);

        if (!rev.getStatus().equals(RevisaoStatusEnum.INICIADA)) {
            throw new InvalidStageException("Apenas Revisões Iniciadas podem ser Rejeitadas.");
        }

        rev.setStatus(RevisaoStatusEnum.REJEITADA);

        Empreendimento emp = rev.getEmpreendimento();
        emp.setStatus(EmpStatusEnum.EM_ELABORACAO);

        empreendimentoRepository.save(emp);
        revisaoRepository.save(rev);
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
        documentSearch.updateMany(marcaRevIds, update, MarcaRevDocElement.class);
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
}