package com.squad13.apimonolito.services.editor;

import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.LocalDocElement;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.editor.LocalDocElementRepository;
import com.squad13.apimonolito.repository.catalog.AmbienteRepository;
import com.squad13.apimonolito.repository.catalog.ItemRepository;
import com.squad13.apimonolito.repository.catalog.MarcaRepository;
import com.squad13.apimonolito.repository.catalog.MaterialRepository;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EspecificacaoService {
    private final EmpreendimentoRepository empRepository;

    private final AmbienteRepository ambienteRepository;
    private final MarcaRepository marcaRepository;
    private final MaterialRepository materialRepository;
    private final ItemRepository itemRepository;

    private final EspecificacaoDocRepository especRepository;
    private final LocalDocElementRepository localDocRepository;

    private Map<Class<?>, JpaRepository<?, Long>> catalogRepoMap;

    @PostConstruct
    void init() {
        catalogRepoMap = Map.of(
                Ambiente.class, ambienteRepository,
                Marca.class, marcaRepository,
                Material.class, materialRepository,
                ItemDesc.class, itemRepository
        );
    }

    private EspecificacaoDoc findEspecById(String id) {
        return especRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontradas para o ID: "));
    }

    public List<EspecificacaoDoc> getAll() {
        return especRepository.findAll();
    }

    public EspecificacaoDoc getById(String id) {
        return especRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada para o ID: " + id));
    }

    public EspecificacaoDoc create(Long empId) {
        Empreendimento emp = empRepository.findById(empId).orElseThrow(() -> new ResourceNotFoundException("A"));

        EspecificacaoDoc eDoc = new EspecificacaoDoc();
        eDoc.setName(emp.getName());
        eDoc.setEmpreendimentoId(emp.getId());

        return especRepository.save(eDoc);
    }

    @SuppressWarnings("unchecked")
    private <T> T findInCatalog(DocElementDTO dto) {
        Class<?> catalogClass = dto.type().getCatalogClass();

        if (catalogClass == null) return null;

        JpaRepository<T, Long> repo = (JpaRepository<T, Long>) catalogRepoMap.get(catalogClass);

        if (repo == null) {
            throw new IllegalStateException("No repository found for catalog type: " + catalogClass.getSimpleName());
        }

        return repo.findById(dto.catalogId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catalog entry not found for id " + dto.catalogId() + " (" + catalogClass.getSimpleName() + ")"
                ));
    }

    public EspecificacaoDoc addElement(DocElementDTO dto) {
        EspecificacaoDoc eDoc = findEspecById(dto.especId());
        DocElement element = dto.type().newInstance();
        element.setEspecificacaoDoc(eDoc);

        Object catalogEntity = findInCatalog(dto);
        if (catalogEntity != null) {
            element.setCatalogId(dto.catalogId());
        }

        if (element instanceof LocalDocElement local) {
            if (eDoc.getLocais().stream().anyMatch(l -> l.getLocal() == dto.local())) {
                throw new ResourceAlreadyExistsException(
                        "O local '" + dto.local() + "' já existe no documento."
                );
            }

            local.setLocal(dto.local());
            eDoc.getLocais().add(localDocRepository.save(local));
            return especRepository.save(eDoc);
        }

        return especRepository.save(eDoc);
    }
}
