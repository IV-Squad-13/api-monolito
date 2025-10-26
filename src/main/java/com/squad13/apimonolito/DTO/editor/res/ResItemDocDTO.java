package com.squad13.apimonolito.DTO.editor.res;


import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
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

    public static ResItemDocDTO fromDoc(ItemDocElement doc) {
        ResItemDocDTO item = ResDocElementDTO.fromDoc(doc, ResItemDocDTO.class);
        item.setDesc(doc.getDesc());
        item.setType(doc.getType());
        item.setTypeId(doc.getTypeId());
        return item;
    }
}