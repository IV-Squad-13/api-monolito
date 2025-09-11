package com.squad13.apimonolito.models.catalog;

import com.squad13.apimonolito.dto.res.SpecAssociationResDTO;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "tb_ambiente")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_ambiente")),
        @AttributeOverride(name = "name", column = @Column(name = "nm_ambiente"))
})
public class Ambiente extends CatalogEntity {

    @OneToMany(mappedBy = "ambiente")
    private Set<ItemAmbiente> itemSet;

    @Override
    public List<SpecAssociationResDTO> getAssociations() {
        return itemSet.stream().map(rel -> {
                return new SpecAssociationResDTO(
                        rel.getId(),
                        rel.getItem().getId(),
                        rel.getItem().getName(),
                        rel.getItem().getIsActive(),
                        rel.getIsRequired()
                );
        }).collect(Collectors.toList());
    }
}
