package com.squad13.apimonolito.util;

import com.squad13.apimonolito.DTO.editor.AmbienteDocDTO;
import com.squad13.apimonolito.DTO.editor.DocElementDTO;
import com.squad13.apimonolito.DTO.editor.ItemDocDTO;
import com.squad13.apimonolito.exceptions.InvalidDocumentTypeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.ItemType;
import com.squad13.apimonolito.models.editor.mongo.*;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.mongo.editor.*;
import com.squad13.apimonolito.util.enums.DocElementEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class DocElementFactory {

    private final CatalogSearch catalogSearch;
    private final DocumentSearch docSearch;

    private final AmbienteDocElementRepository ambienteRepo;
    private final ItemDocElementRepository itemRepo;
    private final MaterialDocElementRepository materialRepo;
    private final MarcaDocElementRepository marcaRepo;
    private final EspecificacaoDocRepository especRepo;

    private final Map<DocElementEnum, BiFunction<EspecificacaoDoc, DocElementDTO, DocElement>> creators = Map.of(
            DocElementEnum.AMBIENTE, this::createAmbiente,
            DocElementEnum.ITEM, this::createItem,
            DocElementEnum.MATERIAL, this::createMaterial,
            DocElementEnum.MARCA, (espec, dto) -> createMarca(dto)
    );
    private final LocalDocRepository localDocRepository;

    public DocElement create(EspecificacaoDoc espec, DocElementDTO dto) {
        BiFunction<EspecificacaoDoc, DocElementDTO, DocElement> creator = creators.get(dto.getDocType());
        if (creator == null) {
            throw new InvalidDocumentTypeException("Tipo de elemento não suportado: " + dto.getDocType());
        }
        return creator.apply(espec, dto);
    }

    private AmbienteDocElement createAmbiente(EspecificacaoDoc espec, DocElementDTO dto) {
        AmbienteDocDTO ambienteDTO = (AmbienteDocDTO) dto;
        AmbienteDocElement ambiente = AmbienteDocElement.fromDto(ambienteDTO, espec);

        boolean alreadyExists = espec.getLocais().stream()
                .filter(local -> local.getLocal().equals(ambienteDTO.getLocal()))
                .flatMap(local -> local.getAmbienteDocList().stream())
                .anyMatch(existing -> existing.getName().equalsIgnoreCase(ambiente.getName()));

        if (alreadyExists) {
            throw new ResourceAlreadyExistsException(
                    "O ambiente '" + ambiente.getName() + "' já existe em " + espec.getName()
            );
        }

        AmbienteDocElement savedAmbiente = ambienteRepo.save(ambiente);

        LocalDoc local = espec.getLocais().stream()
                .filter(l -> l.getLocal().equals(savedAmbiente.getLocal()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Local não encontrado: " + savedAmbiente.getLocal()
                ));

        local.getAmbienteDocList().add(savedAmbiente);

        localDocRepository.save(local);
        return savedAmbiente;
    }

    private ItemDocElement createItem(EspecificacaoDoc espec, DocElementDTO dto) {
        ItemDocDTO itemDTO = (ItemDocDTO) dto;
        ItemType type = catalogSearch.findInCatalog(itemDTO.getTypeId(), ItemType.class);
        ItemDocElement item = ItemDocElement.fromDto(itemDTO, espec, type);

        if (itemDTO.getPrevId() != null) {
            AmbienteDocElement ambiente = docSearch.findInDocument(itemDTO.getPrevId(), AmbienteDocElement.class);

            boolean alreadyExists = ambiente.getItemDocList().stream()
                    .anyMatch(existing -> existing.getName().equalsIgnoreCase(item.getName()));

            if (alreadyExists) {
                throw new ResourceAlreadyExistsException(
                        "O item '" + item.getName() + "' já existe em " + ambiente.getName()
                );
            }

            ambiente.getItemDocList().add(item);
            ambienteRepo.save(ambiente);
        }

        return itemRepo.save(item);
    }

    private MaterialDocElement createMaterial(EspecificacaoDoc espec, DocElementDTO dto) {
        MaterialDocElement material = MaterialDocElement.fromDto(dto, espec);

        boolean alreadyExists = espec.getMateriais().stream()
                .anyMatch(existing -> existing.getName().equalsIgnoreCase(material.getName()));

        if (alreadyExists) {
            throw new ResourceAlreadyExistsException(
                    "O material '" + material.getName() + "' já existe em " + espec.getName()
            );
        }

        materialRepo.save(material);
        espec.getMateriais().add(material);
        especRepo.save(espec);
        return material;
    }

    private MarcaDocElement createMarca(DocElementDTO dto) {
        MarcaDocElement marca = MarcaDocElement.fromDto(dto);

        if (dto.getPrevId() != null) {
            MaterialDocElement material = docSearch.findInDocument(dto.getPrevId(), MaterialDocElement.class);

            boolean alreadyExists = material.getMarcaDocList().stream()
                    .anyMatch(existing -> existing.getName().equalsIgnoreCase(marca.getName()));

            if (alreadyExists) {
                throw new ResourceAlreadyExistsException(
                        "A marca '" + marca.getName() + "' já existe em " + material.getName()
                );
            }

            material.getMarcaDocList().add(marca);
            materialRepo.save(material);
        }

        return marcaRepo.save(marca);
    }
}
