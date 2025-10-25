package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.LoadCatalogParamsDTO;
import com.squad13.apimonolito.DTO.catalog.edit.EditItemDTO;
import com.squad13.apimonolito.DTO.catalog.ItemDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemDTO;
import com.squad13.apimonolito.exceptions.InvalidAttributeException;
import com.squad13.apimonolito.exceptions.ResourceAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.ItemType;
import com.squad13.apimonolito.repository.catalog.ItemRepository;
import com.squad13.apimonolito.repository.catalog.ItemTypeRepository;
import com.squad13.apimonolito.util.mapper.CatalogMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ItemService {

    @PersistenceContext
    private final EntityManager em;

    private final CatalogMapper catalogMapper;

    private final ItemRepository itemRepository;

    private final ItemTypeRepository itemTypeRepository;

    public List<ResItemDTO> findAll(LoadCatalogParamsDTO loadDTO) {
        return itemRepository.findAll()
                .stream()
                .map(item -> catalogMapper.toResponse(item, loadDTO))
                .toList();
    }

    private ItemDesc findByIdOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item com ID: " + id + " não encontrado."));
    }

    public ResItemDTO findById(Long id, LoadCatalogParamsDTO loadDTO) {
        return itemRepository.findById(id)
                .map(item -> catalogMapper.toResponse(item, loadDTO))
                .orElseThrow(() -> new ResourceNotFoundException("Item com ID: " + id + " não encontrado."));
    }

    public List<ResItemDTO> findByAttribute(String attribute, String value, LoadCatalogParamsDTO loadDTO) {
        boolean attributeExists = Arrays.stream(ItemDesc.class.getDeclaredFields())
                .anyMatch(f -> f.getName().equals(attribute));

        if (!attributeExists)
            throw new InvalidAttributeException("Atributo inválido: " + attribute);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ItemDesc> cq = cb.createQuery(ItemDesc.class);
        Root<ItemDesc> root = cq.from(ItemDesc.class);

        Predicate predicate;

        if (attribute.equals("type")) {
            Join<ItemDesc, ItemType> itemType = root.join("type", JoinType.INNER);

            if (value == null) {
                predicate = cb.isNull(itemType.get("name"));
            } else {
                predicate = cb.like(cb.lower(itemType.get("name")), value.toLowerCase());
            }

        } else {
            if (value == null) {
                predicate = cb.isNull(root.get(attribute));
            } else {
                predicate = cb.like(cb.lower(root.get(attribute)), value.toLowerCase());
            }
        }

        cq.select(root).where(predicate);
        return em.createQuery(cq).getResultList()
                .stream()
                .map(item -> catalogMapper.toResponse(item, loadDTO))
                .toList();
    }

    public ResItemDTO createItem(ItemDTO dto) {
        ItemType type = itemTypeRepository.findByIdOrName(dto.getTypeId(), dto.getName())
                        .orElse(null);

        itemRepository.findByNameAndDescAndType(dto.getName(), dto.getDesc(), type)
                .ifPresent(i -> {
                    throw new ResourceAlreadyExistsException(
                            "Já existe um item com a descrição " + dto.getDesc() + " do tipo " + dto.getType()
                    );
                });

        ItemDesc item = new ItemDesc();
        item.setName(dto.getName());
        item.setIsActive(dto.getIsActive());
        item.setDesc(dto.getDesc());
        item.setType(type);

        ItemDesc saved = itemRepository.save(item);
        return catalogMapper.toResponse(saved, LoadCatalogParamsDTO.allTrue());
    }

    public ResItemDTO updateItem(Long id, EditItemDTO dto) {
        ItemDesc item = findByIdOrThrow(id);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }
        if (dto.getDesc() != null && !dto.getDesc().isBlank()) {
            item.setDesc(dto.getDesc());
        }
        if (dto.getIsActive() != null) {
            item.setIsActive(dto.getIsActive());
        }
        if (dto.getType() != null || dto.getTypeId() != null) {
            itemTypeRepository.findByIdOrName(dto.getTypeId(), dto.getType())
                    .ifPresent(item::setType);
        }

        ItemDesc updated = itemRepository.save(item);
        return catalogMapper.toResponse(updated, LoadCatalogParamsDTO.allTrue());
    }

    public void deleteItem(Long id) {
        ItemDesc item = findByIdOrThrow(id);
        itemRepository.delete(item);
    }

    public ResItemDTO deactivateItem(Long id) {
        ItemDesc existing = findByIdOrThrow(id);
        existing.setIsActive(false);
        return catalogMapper.toResponse(itemRepository.save(existing), LoadCatalogParamsDTO.allTrue());
    }
}
