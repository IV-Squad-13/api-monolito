package com.squad13.apimonolito.DTO.revision;

import com.squad13.apimonolito.util.enums.DocElementEnum;
import com.squad13.apimonolito.util.enums.LocalEnum;
import com.squad13.apimonolito.util.enums.RevDocElementEnum;
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
public class RevDocSearchParamsDTO {

    private RevDocElementEnum docType;
    private String id;
    private String revisedDocId;
    private Long revisionId;
    private Boolean isApproved;
    private String comment;
    private Instant created;
    private Instant updated;

    // Para Itens
    private Boolean isDescApproved;
    private Boolean isTypeApproved;

    // TODO: Colocar parâmetros de especificação

    public RevDocSearchParamsDTO(RevDocElementEnum docType, ObjectId id) {
        this.docType = docType;
        this.id = id.toHexString();
    }

    public RevDocSearchParamsDTO(RevDocElementEnum docType, Long revisionId) {
        this.docType = docType;
        this.revisionId = revisionId;
    }

    public Map<String, Object> buildFilters(boolean unsetApproval) {
        Map<String, Object> filters = new HashMap<>();

        if (this.getId() != null)
            filters.put("id", this.getId());

        if (this.getRevisedDocId() != null)
            filters.put("revisedDocId", this.getRevisedDocId());

        if (this.getRevisionId() != null)
            filters.put("revisionId", this.getRevisionId());

        if (this.getIsApproved() != null || unsetApproval)
            filters.put("isApproved", this.getIsApproved());

        if (this.getComment() != null)
            filters.put("comment", this.getComment());

        if (this.getCreated() != null)
            filters.put("created", this.getCreated());

        if (this.getUpdated() != null)
            filters.put("updated", this.getUpdated());

        if (this.getIsDescApproved() != null)
            filters.put("isDescApproved", this.getIsDescApproved());

        if (this.getIsTypeApproved() != null)
            filters.put("isTypeApproved", this.getIsTypeApproved());

        return filters;
    }
}

