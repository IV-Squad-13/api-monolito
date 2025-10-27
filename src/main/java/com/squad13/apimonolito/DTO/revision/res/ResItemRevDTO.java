package com.squad13.apimonolito.DTO.revision.res;

import com.squad13.apimonolito.DTO.editor.res.ResDocElementDTO;
import com.squad13.apimonolito.DTO.editor.res.ResItemDocDTO;
import com.squad13.apimonolito.DTO.editor.res.ResMarDocDTO;
import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.models.revision.mongo.AmbienteRevDocElement;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDocElement;
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

    public static ResItemRevDTO fromDoc(ItemRevDocElement doc) {
        ResItemRevDTO dto = ResRevDocDTO.fromDoc(doc, ResItemRevDTO::new);
        dto.setRevisedDoc(ResItemDocDTO.fromDoc(doc.getDoc()));
        dto.setIsDescApproved(doc.getIsDescApproved());
        dto.setIsTypeApproved(doc.getIsTypeApproved());
        return dto;
    }
}