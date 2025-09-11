package com.squad13.apimonolito.models.catalog;

import com.squad13.apimonolito.dto.req.SpecReqDTO;
import com.squad13.apimonolito.dto.res.SpecAssociationResDTO;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_marca")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_marca")),
        @AttributeOverride(name = "name", column = @Column(name = "nm_marca"))
})
public class Marca extends CatalogEntity {

    @OneToMany(mappedBy = "marca")
    private Set<MarcaMaterial> materialSet;

    public Marca(String name) {
        super(name);
    }

    @Override
    public List<SpecAssociationResDTO> getAssociations() {
        return materialSet.stream().map(rel -> new SpecAssociationResDTO(
                rel.getId(),
                rel.getMaterial().getId(),
                rel.getMaterial().getName(),
                rel.getMaterial().getIsActive(),
                rel.isRequired()
        )).collect(Collectors.toList());
    }

    @Override
    public Marca toEntity(SpecReqDTO specReqDTO) {
        return new Marca(
                specReqDTO.name()
        );
    }
}
