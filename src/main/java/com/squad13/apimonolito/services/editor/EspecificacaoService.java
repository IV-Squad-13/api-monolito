package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.DTO.editor.EspecificacaoDocDTO;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.editor.LocalDocRepository;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.util.DocElementFactory;
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

    private final DocElementFactory docElementFactory;

    private final EmpreendimentoRepository empRepository;
    private final EspecificacaoDocRepository especRepository;

    private final LocalDocRepository localDocRepository;

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
            default -> throw new IllegalArgumentException("Tipo de inicialização inválido: " + dto.initType());
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

    public DocElement createRawElement(String id, DocElementDTO dto) {
        EspecificacaoDoc espec = getById(id);

        return docElementFactory.create(espec, dto);
    }
}