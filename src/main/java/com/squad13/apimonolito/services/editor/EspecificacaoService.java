package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.DTO.editor.AmbienteDocDTO;
import com.squad13.apimonolito.DTO.editor.CatalogRelationDTO;
import com.squad13.apimonolito.DTO.editor.ItemDocDTO;
import com.squad13.apimonolito.DTO.editor.LocalDocDTO;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.mongo.editor.*;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.util.CatalogSearch;
import com.squad13.apimonolito.util.ReflectionUtils;
import com.squad13.apimonolito.util.enums.CatalogRelationEnum;
import com.squad13.apimonolito.util.mappers.EditorMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EspecificacaoService {

    private final EmpreendimentoRepository empRepository;
    private final EspecificacaoDocRepository especRepository;
    private final AmbienteDocElementRepository ambienteDocRepository;
    private final ItemDocElementRepository itemDocRepository;
    private final MarcaDocElementRepository marcaDocRepository;

    private final CatalogSearch catalogSearch;

    private final EditorMapper editorMapper;
    private final MaterialDocElementRepository materialDocRepository;

    public List<EspecificacaoDoc> getAll() {
        return especRepository.findAll();
    }

    public EspecificacaoDoc getById(String id) {
        return especRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o ID: " + id));
    }

    public EspecificacaoDoc create(Long empId) {
        Empreendimento emp = empRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Empreendimento não encontrado com o ID: " + empId));

        EspecificacaoDoc eDoc = new EspecificacaoDoc();
        eDoc.setName(emp.getName());
        eDoc.setEmpreendimentoId(emp.getId());

        return especRepository.save(eDoc);
    }

    private Object saveDocElement(Object doc) {
        if (doc instanceof AmbienteDocElement ambiente) return ambienteDocRepository.save(ambiente);
        if (doc instanceof ItemDocElement item) return itemDocRepository.save(item);
        if (doc instanceof MarcaDocElement marca) return marcaDocRepository.save(marca);
        if (doc instanceof MaterialDocElement material) return materialDocRepository.save(material);
        throw new IllegalArgumentException("Unsupported document type: " + doc.getClass().getSimpleName());
    }

    private void linkDocuments(EspecificacaoDoc espec, Object sourceDoc, Object targetDoc) {
        if (sourceDoc instanceof AmbienteDocElement ambiente && targetDoc instanceof ItemDocElement item) {
            ambiente.getItemDocList().add(item);
            espec.getLocais().stream()
                    .filter(l -> l.getLocal().equals(ambiente.getLocal()))
                    .findFirst()
                    .ifPresent(l -> l.getAmbienteDocList().add(ambiente));
        } else if (sourceDoc instanceof MarcaDocElement marca && targetDoc instanceof MaterialDocElement material) {
            material.getMarcaDocList().add(marca);
            espec.getMateriais().add(material);
        } else {
            throw new IllegalArgumentException("Unsupported relation combination: "
                    + sourceDoc.getClass().getSimpleName() + " -> " + targetDoc.getClass().getSimpleName());
        }
    }

    @Transactional
    public EspecificacaoDoc addElementFromCatalog(String id, CatalogRelationDTO dto) {
        EspecificacaoDoc espec = getById(id);
        CatalogRelationEnum relationType = dto.type();

        Object relation = catalogSearch.findAssociativeInCatalog(
                dto.sourceId(),
                dto.targetId(),
                relationType.getRelationClass(),
                relationType.getSourceField(),
                relationType.getTargetField()
        );

        Object sourceEntity = ReflectionUtils.getFieldValue(relation, relationType.getSourceField());
        Object targetEntity = ReflectionUtils.getFieldValue(relation, relationType.getTargetField());

        Object sourceDoc = relationType.getSourceMapper().apply(editorMapper, espec, sourceEntity);
        Object targetDoc = relationType.getTargetMapper().apply(editorMapper, espec, targetEntity);

        sourceDoc = saveDocElement(sourceDoc);
        targetDoc = saveDocElement(targetDoc);

        linkDocuments(espec, sourceDoc, targetDoc);

        return especRepository.save(espec);
    }
}