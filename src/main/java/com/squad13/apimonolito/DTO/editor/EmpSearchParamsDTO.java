package com.squad13.apimonolito.DTO.editor;

import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.enums.EmpStatusEnum;
import com.squad13.apimonolito.util.enums.LocalEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springdoc.core.annotations.ParameterObject;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ParameterObject
@NoArgsConstructor
@AllArgsConstructor
public class EmpSearchParamsDTO {

    private Long id;
    private String name;
    private EmpStatusEnum status;

    public static Map<String, Object> buildFilters(EmpSearchParamsDTO params) {
        Map<String, Object> filters = new HashMap<>();

        if (params.getId() != null)
            filters.put("id", params.getId());

        if (params.getName() != null && !params.getName().isEmpty())
            filters.put("name", params.getName());

        if (params.getStatus() != null)
            filters.put("status", params.getStatus());
        return filters;
    }
}