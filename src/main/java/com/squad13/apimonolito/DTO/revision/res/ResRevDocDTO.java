package com.squad13.apimonolito.DTO.revision.res;

import com.squad13.apimonolito.DTO.editor.res.ResDocElementDTO;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import com.squad13.apimonolito.models.revision.structures.RevDocElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.function.Supplier;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResRevDocDTO {

    private String id;
    private String revisedDocId;
    private Long revisionId;
    private Boolean isApproved;
    private String comment;

    public static <D extends RevDocElement, T extends ResRevDocDTO>
    T fromDoc(D doc, Supplier<T> factory) {
        T instance = factory.get();
        instance.setId(doc.getId().toHexString());
        instance.setRevisedDocId(doc.getRevisedDocId().toHexString());
        instance.setRevisionId(doc.getRevisionId());
        instance.setIsApproved(doc.getIsApproved());
        instance.setComment(doc.getComment());
        return instance;
    }

    public ObjectId getObjectId() {
        return new ObjectId(id);
    }
}