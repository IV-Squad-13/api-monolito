package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ParameterObject
@NoArgsConstructor
@AllArgsConstructor
public class DocSearchParamsDTO {

    private DocElementEnum docType;
    private String id;
    private String name;
    private Long catalogId;
    private String especificacaoId;
    private String parentId;
    private boolean inSync;
    private Instant created;
    private Instant updated;

    // Para Ambientes
    private LocalEnum local;

    // Para Itens
    private String desc;
    private Long typeId;
    private String type;

    public DocSearchParamsDTO(DocElementEnum docType, ObjectId id) {
        this.docType = docType;
        this.id = id.toHexString();
    }

    public static Map<String, Object> buildFilters(DocSearchParamsDTO params) {
        Map<String, Object> filters = new HashMap<>();

        if (params.getId() != null)
            filters.put("id", params.getId());

        if (params.getName() != null)
            filters.put("name", params.getName());

        if (params.getCatalogId() != null)
            filters.put("catalogId", params.getCatalogId());

        if (params.getEspecificacaoId() != null)
            filters.put("especificacaoId", params.getEspecificacaoId());

        if (params.getParentId() != null)
            filters.put("parentId", params.getParentId());

        if (params.isInSync())
            filters.put("inSync", true);

        if (params.getCreated() != null)
            filters.put("created", params.getCreated());

        if (params.getUpdated() != null)
            filters.put("updated", params.getUpdated());

        if (params.getLocal() != null)
            filters.put("local", params.getLocal());

        if (params.getDesc() != null)
            filters.put("desc", params.getDesc());

        if (params.getTypeId() != null)
            filters.put("typeId", params.getTypeId());

        if (params.getType() != null)
            filters.put("type", params.getType());

        return filters;
    }
}

