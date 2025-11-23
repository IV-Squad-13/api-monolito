package com.squad13.apimonolito.DTO.revision.res;

import com.squad13.apimonolito.DTO.editor.res.ResDocElementDTO;
import com.squad13.apimonolito.DTO.editor.res.ResItemDocDTO;
import com.squad13.apimonolito.DTO.editor.res.ResMarDocDTO;
import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.models.revision.mongo.AmbienteRevDocElement;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDocElement;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResItemRevDTO extends ResRevDocDTO {

    private ResItemDocDTO revisedDoc;

    private Boolean isDescApproved;
    private Boolean isTypeApproved;

    @Override
    public void populateExtraFields(RevDocElement d) {
        ItemRevDocElement doc = (ItemRevDocElement) d;
        this.isDescApproved = doc.getIsDescApproved();
        this.isTypeApproved = doc.getIsTypeApproved();
    }

    public static ResItemRevDTO fromDoc(ItemRevDocElement doc) {
        ResItemRevDTO dto = ResRevDocDTO.fromDoc(doc, ResItemRevDTO::new);
        if (doc.getDoc() != null) {
            dto.setRevisedDoc(ResItemDocDTO.fromDoc(doc.getDoc()));
        }

        dto.setIsDescApproved(doc.getIsDescApproved());
        dto.setIsTypeApproved(doc.getIsTypeApproved());
        return dto;
    }
}