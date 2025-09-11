package com.squad13.apimonolito.models.catalog;

import com.squad13.apimonolito.dto.req.SpecReqDTO;
import com.squad13.apimonolito.dto.res.SpecAssociationResDTO;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_item")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_item")),
        @AttributeOverride(name = "name", column = @Column(name = "nm_item"))
})
public class Item extends CatalogEntity {

    @Column(name = "ds_item", length = 60, nullable = false)
    private String desc;

    @OneToMany(mappedBy = "item")
    private Set<ItemAmbiente> ambienteSet;

    public Item(String name, String desc) {
        super(name);
        this.desc = desc;
    }

    @Override
    public List<SpecAssociationResDTO> getAssociations() {
        return ambienteSet.stream().map(rel -> new SpecAssociationResDTO(
                rel.getId(),
                rel.getAmbiente().getId(),
                rel.getAmbiente().getName(),
                rel.getAmbiente().getIsActive(),
                rel.getIsRequired()
        )).collect(Collectors.toList());
    }

    @Override
    public Item toEntity(SpecReqDTO specReqDTO) {
        return new Item(
                specReqDTO.name(),
                specReqDTO.itemDesc()
        );
    }
}
