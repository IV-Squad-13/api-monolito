package com.squad13.apimonolito.DTO.editor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ParameterObject
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacaoSearchParamsDTO {

    private String id;
    private Long empreendimentoId;
    private String name;
    private String desc;
    private String obs;
    private Instant created;
    private Instant updated;

    // TODO: busca por associação
    // private String childId;

    public EspecificacaoSearchParamsDTO(Long empId) {
        this.empreendimentoId = empId;
    }

    public static Map<String, Object> buildFilters(EspecificacaoSearchParamsDTO params) {
        Map<String, Object> filters = new HashMap<>();

        if (params.getId() != null)
            filters.put("id", params.getId());

        if (params.getEmpreendimentoId() != null)
            filters.put("empreendimentoId", params.getEmpreendimentoId());

        if (params.getName() != null)
            filters.put("name", params.getName());

        if (params.getDesc() != null)
            filters.put("desc", params.getDesc());

        if (params.getObs() != null)
            filters.put("obs", params.getObs());

        if (params.getCreated() != null)
            filters.put("created", params.getCreated());

        if (params.getUpdated() != null)
            filters.put("updated", params.getUpdated());

        return filters;
    }
}