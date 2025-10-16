package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.DTO.editor.EspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.edit.EditEspecificacaoDocDTO;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.mongo.editor.*;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EspecificacaoService {

    private final EmpreendimentoRepository empRepository;

    private final EspecificacaoDocRepository especRepository;

    private final LocalDocRepository localDocRepository;
    private final AmbienteDocElementRepository ambienteDocRepository;
    private final ItemDocElementRepository itemDocRepository;
    private final MaterialDocElementRepository materialDocRepository;
    private final MarcaDocElementRepository marcaDocRepository;

    public List<EspecificacaoDoc> getAll() {
        return especRepository.findAll();
    }

    public EspecificacaoDoc getById(String id) {
        return especRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o ID: " + id));
    }

    public EspecificacaoDoc create(EspecificacaoDocDTO dto) {
        if (!empRepository.existsById(dto.empId())) {
            throw new ResourceNotFoundException(
                    "Empreendimento não encontrado com o ID: " + dto.empId()
            );
        }

        return switch (dto.initType()) {
            case AVULSO -> createEspecificacaoAvulso(dto);
            case PADRAO -> createFromPadrao(dto); // TODO: implementar
            case IMPORT -> createFromImport(dto); // TODO: implementar
        };
    }

    private EspecificacaoDoc createEspecificacaoAvulso(EspecificacaoDocDTO dto) {
        EspecificacaoDoc espec = new EspecificacaoDoc();
        espec.setName(dto.name());
        espec.setEmpreendimentoId(dto.empId());
        espec.setDesc(dto.desc());
        espec.setObs(dto.obs());
        especRepository.save(espec);

        List<LocalDoc> locais = Arrays.stream(LocalEnum.values())
                .map(localEnum -> {
                    LocalDoc local = new LocalDoc();
                    local.setEspecificacaoDoc(espec);
                    local.setLocal(localEnum);
                    return localDocRepository.save(local);
                })
                .toList();

        espec.setLocais(locais);

        return especRepository.save(espec);
    }

    private EspecificacaoDoc createFromPadrao(EspecificacaoDocDTO dto) {
        // TODO: implementar inicialização por padrão
        throw new UnsupportedOperationException("Inicialização por PADRAO ainda não implementada");
    }

    private EspecificacaoDoc createFromImport(EspecificacaoDocDTO dto) {
        // TODO: implementar inicialização por importação
        throw new UnsupportedOperationException("Inicialização por IMPORT ainda não implementada");
    }

    public EspecificacaoDoc update(String id, EditEspecificacaoDocDTO dto) {
        EspecificacaoDoc espec = getById(id);

        if (dto.name() != null && !dto.name().isEmpty()) {
            espec.setName(dto.name());
        }

        if (dto.desc() != null) {
            espec.setDesc(dto.desc());
        }

        if (dto.obs() != null) {
            espec.setObs(dto.obs());
        }

        if (dto.empId() != null) {
            espec.setEmpreendimentoId(dto.empId());
        }

        return especRepository.save(espec);
    }

    public void delete(String id) {
        EspecificacaoDoc espec = getById(id);

        itemDocRepository.deleteAllByEspecificacaoDoc(espec);
        ambienteDocRepository.deleteAllByEspecificacaoDoc(espec);
        localDocRepository.deleteAllByEspecificacaoDoc(espec);
        marcaDocRepository.deleteAllByEspecificacaoDoc(espec);
        materialDocRepository.deleteAllByEspecificacaoDoc(espec);

        especRepository.delete(espec);
    }
}