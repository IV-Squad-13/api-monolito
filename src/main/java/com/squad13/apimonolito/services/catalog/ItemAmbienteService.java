package com.squad13.apimonolito.services.catalog;

import com.squad13.apimonolito.DTO.catalog.res.ResAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemAmbienteDTO;
import com.squad13.apimonolito.DTO.catalog.res.ResItemDTO;
import com.squad13.apimonolito.exceptions.AssociationAlreadyExistsException;
import com.squad13.apimonolito.exceptions.ResourceNotFoundException;
import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.repository.catalog.AmbienteRepository;
import com.squad13.apimonolito.repository.catalog.ItemAmbieteRepository;
import com.squad13.apimonolito.repository.catalog.ItemRepository;
import com.squad13.apimonolito.util.Mapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ItemAmbienteService {

    private final ItemAmbieteRepository itemAmbieteRepository;
    private final ItemRepository itemRepository;
    private final AmbienteRepository ambienteRepository;

    private final Mapper mapper;

    public List<ResItemDTO> findAmbienteItems(Long id) {
        return itemAmbieteRepository.findByAmbiente_Id(id)
                .stream()
                .map(ItemAmbiente::getItemDesc)
                .map(item -> mapper.toResponse(item, false))
                .toList();
    }

    public List<ResAmbienteDTO> findItemAmbientes(Long id) {
        return itemAmbieteRepository.findByItemDesc_Id(id)
                .stream().map(ItemAmbiente::getAmbiente)
                .map(ambiente -> mapper.toResponse(ambiente, false))
                .toList();
    }

    private ItemDesc findItemByIdOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nenhum item com ID: " + id + " foi encontrado"));
    }

    private Ambiente findAmbienteByIdOrThrow(Long id) {
        return ambienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nenhum ambiente com ID: " + id + " foi encontrado"));
    }

    public ResItemAmbienteDTO associateItemAndAmbiente(Long itemId, Long ambienteId) {
        ItemDesc item = findItemByIdOrThrow(itemId);
        Ambiente ambiente = findAmbienteByIdOrThrow(ambienteId);

        itemAmbieteRepository.findByItemDescAndAmbiente(item, ambiente)
                .ifPresent(rel -> {
                    throw new AssociationAlreadyExistsException(
                            "Associação já existente entre o item " + itemId + " e o ambiente " + ambienteId);
                });

        ItemAmbiente itemAmbiente = new ItemAmbiente();
        itemAmbiente.setAmbiente(ambiente);
        itemAmbiente.setItemDesc(item);

        ambiente.getItemSet().add(itemAmbiente);
        ambienteRepository.save(ambiente);

        return mapper.toResponse(itemAmbiente);
    }

    public void deleteItemAndAmbienteAssociation(Long itemId, Long ambienteId) {
        ItemDesc item = findItemByIdOrThrow(itemId);
        Ambiente ambiente = findAmbienteByIdOrThrow(ambienteId);

        ItemAmbiente itemAmbiente = itemAmbieteRepository.findByItemDescAndAmbiente(item, ambiente)
                .orElseThrow(() -> new ResourceNotFoundException("Associação entre o item " + itemId + " e o ambiente " + ambienteId + " não encontrada"));
        itemAmbieteRepository.delete(itemAmbiente);
    }
}
