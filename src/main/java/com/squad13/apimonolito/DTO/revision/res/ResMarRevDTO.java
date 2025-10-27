package com.squad13.apimonolito.DTO.revision.res;

import com.squad13.apimonolito.DTO.editor.res.ResMarDocDTO;
import com.squad13.apimonolito.DTO.editor.res.ResMatDocDTO;
import com.squad13.apimonolito.models.revision.mongo.MarcaRevDocElement;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResMarRevDTO extends ResRevDocDTO {

    private ResMarDocDTO revisedDoc;

    public static ResMarRevDTO fromDoc(MarcaRevDocElement doc) {
        ResMarRevDTO resMarRevDTO = ResRevDocDTO.fromDoc(doc, ResMarRevDTO::new);
        resMarRevDTO.setRevisedDoc(ResMarDocDTO.fromDoc(doc.getDoc()));
        return resMarRevDTO;
    }
}