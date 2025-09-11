package com.squad13.apimonolito.models.catalog;

import com.squad13.apimonolito.dto.res.SpecAssociationResDTO;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "tb_material")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_material")),
        @AttributeOverride(name = "name", column = @Column(name = "nm_material"))
})
public class Material extends CatalogEntity {

    @OneToMany(mappedBy = "material")
    private Set<MarcaMaterial> marcaSet;

    @Override
    public List<SpecAssociationResDTO> getAssociations() {
        return marcaSet.stream().map(rel -> {
            return new SpecAssociationResDTO(
                    rel.getId(),
                    rel.getMarca().getId(),
                    rel.getMarca().getName(),
                    rel.getMarca().getIsActive(),
                    rel.isRequired()
            );
        }).collect(Collectors.toList());
    }
}
