package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.CatalogoDTO;
import com.squad13.apimonolito.DTO.catalog.EditCatalogoDTO;
import com.squad13.apimonolito.DTO.catalog.EditItemTypeDTO;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.Catalogo;
import com.squad13.apimonolito.models.catalog.ItemType;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.repository.catalog.CatalogoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogoService {

    @Autowired
    private CatalogoRepository catalogoRepository;

    public List<Catalogo> findAll() {
        return catalogoRepository.findAll();
    }

    public Optional<Catalogo> findById(Long id) {
        return catalogoRepository.findById(id);
    }

    public Optional<Catalogo> findByName(String name) {
        return catalogoRepository.findByName(name);
    }

    public CatalogoDTO createCatalogo(CatalogoDTO dto) {
        catalogoRepository.findByName(dto.getName())
                .ifPresent(a -> {
                    throw new ResourceAlreadyExistsException(
                            "Já existe um catalogo com o nome " + dto.getName()
                    );
                });

        Catalogo catalogo = new Catalogo();
        catalogo.setName(dto.getName());
        catalogo.setIsActive(dto.isActive());

        return mapToDTO(catalogoRepository.save(catalogo));
    }

    public CatalogoDTO updateCatalogo(EditCatalogoDTO dto) {
        Catalogo catalogo = catalogoRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada para o ID: " + dto.getId()));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            catalogoRepository.findByName(dto.getName())
                    .filter(existing -> !existing.getId().equals(dto.getId()))
                    .ifPresent(existing -> {
                        throw new ResourceAlreadyExistsException(
                                "Já existe um catálogo com o nome " + dto.getName()
                        );
                    });

            catalogo.setName(dto.getName());
        }
        if (dto.getIsActive() != null) {
            catalogo.setIsActive(dto.getIsActive());
        }

        Catalogo updated = catalogoRepository.save(catalogo);
        return mapToDTO(updated);
    }

    public void deleteCatalogo(Long id) {
        Catalogo catalogo = catalogoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catálogo não encontrado para o ID: " + id));

        catalogoRepository.delete(catalogo);
    }

    private CatalogoDTO mapToDTO(Catalogo catalogo) {
        CatalogoDTO dto = new CatalogoDTO();
        dto.setName(catalogo.getName());
        dto.setActive(catalogo.getIsActive());

        return dto;
    }
}