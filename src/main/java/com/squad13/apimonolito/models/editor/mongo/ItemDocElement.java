package com.squad13.apimonolito.models.editor.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.squad13.apimonolito.DTO.editor.ItemDocDTO;
import com.squad13.apimonolito.models.catalog.ItemType;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "items")
@CompoundIndex(name = "item_unique", def = "{'catalogId' : 1, 'name': 1, 'desc': 1, 'type': 1, 'parentId': 1, 'especificacaoDoc': 1}", unique = true)
public class ItemDocElement extends DocElement {

    @NotBlank
    private String desc;

    private String type;

    private Long typeId;

    @Transient
    @JsonProperty
    private List<DocElement> docElementList;

    public static ItemDocElement fromDto(ItemDocDTO dto, EspecificacaoDoc espec, ItemType type) {
        ItemDocElement item = DocElement.genericFromDto(dto, espec, ItemDocElement.class);
        item.setDesc(dto.getDesc());
        item.setTypeId(type.getId());
        item.setType(type.getName());
        return item;
    }
}
