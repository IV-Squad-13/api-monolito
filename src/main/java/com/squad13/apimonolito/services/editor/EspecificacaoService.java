package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.DTO.catalog.res.*;
import com.squad13.apimonolito.DTO.editor.EspecificacaoDocDTO;
import com.squad13.apimonolito.DTO.editor.edit.EditEspecificacaoDocDTO;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.mongo.editor.*;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.services.catalog.ComposicaoService;
import com.squad13.apimonolito.util.enums.LocalEnum;
import com.squad13.apimonolito.util.mappers.EditorMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EspecificacaoService {

    private final EmpreendimentoRepository empRepository;
    private final ComposicaoService compService;

    private final EspecificacaoDocRepository especRepository;

    private final LocalDocRepository localDocRepository;
    private final AmbienteDocElementRepository ambienteDocRepository;
    private final ItemDocElementRepository itemDocRepository;
    private final MaterialDocElementRepository materialDocRepository;
    private final MarcaDocElementRepository marcaDocRepository;
    private final EditorMapper editorMapper;

    public List<EspecificacaoDoc> getAll() {
        return especRepository.findAll();
    }

    public EspecificacaoDoc getById(String id) {
        return especRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o ID: " + id));
    }

    @Transactional
    public EspecificacaoDoc create(EspecificacaoDocDTO dto) {
        if (!empRepository.existsById(dto.empId())) {
            throw new ResourceNotFoundException(
                    "Empreendimento não encontrado com o ID: " + dto.empId()
            );
        }

        return switch (dto.initType()) {
            case AVULSO -> createEspecificacaoAvulso(dto);
            case PADRAO -> createFromPadrao(dto);
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

        espec.setLocais(generateLocais(espec));
        return especRepository.save(espec);
    }

    private List<LocalDoc> generateLocais(EspecificacaoDoc doc) {
        return Arrays.stream(LocalEnum.values())
                .map(localEnum -> {
                    LocalDoc local = new LocalDoc();
                    local.setEspecificacaoDoc(doc);
                    local.setLocal(localEnum);
                    return localDocRepository.save(local);
                })
                .toList();
    }

    private EspecificacaoDoc createFromPadrao(EspecificacaoDocDTO dto) {
        EspecificacaoDoc espec = new EspecificacaoDoc();
        espec.setName(dto.name());
        espec.setEmpreendimentoId(dto.empId());
        espec.setDesc(dto.desc());
        espec.setObs(dto.obs());

        Empreendimento emp = empRepository.findById(dto.empId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Empreendimento não encontrado para o ID: " + dto.empId())
                );

        espec.setId(UUID.randomUUID().toString());

        List<ResItemAmbienteDTO> itemAmbientes = compService.findItensAmbienteByPadrao(emp.getPadrao().getId());
        Map<ResAmbienteDTO, List<ResItemDTO>> groupedAmbientes =
                groupBy(itemAmbientes, ResItemAmbienteDTO::ambiente, ResItemAmbienteDTO::item);

        List<ResMarcaMaterialDTO> marcaMateriais = compService.findMarcasMaterialByPadrao(emp.getPadrao().getId());
        Map<ResMaterialDTO, List<ResMarcaDTO>> groupedMateriais =
                groupBy(marcaMateriais, ResMarcaMaterialDTO::material, ResMarcaMaterialDTO::marca);

        List<AmbienteDocElement> ambientes = editorMapper.structure(
                espec,
                groupedAmbientes,
                editorMapper::fromResponse,
                editorMapper::fromResponse,
                AmbienteDocElement::setItemDocList
        );

        List<MaterialDocElement> materiais = editorMapper.structure(
                espec,
                groupedMateriais,
                editorMapper::fromResponse,
                editorMapper::fromResponse,
                MaterialDocElement::setMarcaDocList
        );

        List<LocalDoc> locais = generateLocais(espec);

        ambientes.forEach(ambiente -> {
            saveAmbienteWithItems(ambiente, locais);
        });

        materiais.forEach(material -> {
            saveMaterialWithMarcas(material, espec);
        });

        List<LocalDoc> savedLocais = localDocRepository.saveAll(locais);
        espec.setLocais(savedLocais);

        return especRepository.save(espec);
    }

    private <T, K, V> Map<K, List<V>> groupBy(
            List<T> items,
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper
    ) {
        return items.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        keyMapper,
                        Collectors.mapping(valueMapper, Collectors.toList())
                ));
    }

    private void saveAmbienteWithItems(AmbienteDocElement ambiente, List<LocalDoc> locais) {
        ambiente.setId(UUID.randomUUID().toString());

        ambiente.getItemDocList().forEach(item -> item.setParentId(ambiente.getId()));

        List<ItemDocElement> savedItems = itemDocRepository.saveAll(ambiente.getItemDocList());
        ambiente.setItemDocList(savedItems);

        ambienteDocRepository.save(ambiente);
        locais.stream()
                .filter(local -> local.getLocal().equals(ambiente.getLocal()))
                .findFirst()
                .ifPresent(local -> local.getAmbienteDocList().add(ambiente));
    }

    private void saveMaterialWithMarcas(MaterialDocElement material, EspecificacaoDoc espec) {
        material.setId(UUID.randomUUID().toString());

        material.getMarcaDocList().forEach(marca -> marca.setParentId(material.getId()));

        List<MarcaDocElement> savedMarcas = marcaDocRepository.saveAll(material.getMarcaDocList());
        material.setMarcaDocList(savedMarcas);

        materialDocRepository.save(material);
        espec.getMateriais().add(material);
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