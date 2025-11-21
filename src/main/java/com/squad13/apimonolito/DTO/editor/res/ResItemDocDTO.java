package com.squad13.apimonolito.DTO.editor.res;


import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.models.editor.structures.DocElement;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResItemDocDTO extends ResDocElementDTO {
    private String desc;
    private String type;
    private Long typeId;

    @Override
    public void populateExtraFields(DocElement d) {
        ItemDocElement doc = (ItemDocElement) d;
        this.desc = doc.getDesc();
        this.type = doc.getType();
        this.typeId = doc.getTypeId();
    }

    public static ResItemDocDTO fromDoc(ItemDocElement doc) {
        ResItemDocDTO item = ResDocElementDTO.fromDoc(doc, ResItemDocDTO::new);
        item.setDesc(doc.getDesc());
        item.setType(doc.getType());
        item.setTypeId(doc.getTypeId());
        return item;
    }
}