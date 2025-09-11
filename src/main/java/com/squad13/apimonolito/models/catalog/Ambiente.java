package com.squad13.apimonolito.models.catalog;

import com.squad13.apimonolito.dto.req.SpecReqDTO;
import com.squad13.apimonolito.dto.res.SpecAssociationResDTO;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.util.enums.LocalEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_ambiente")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_ambiente")),
        @AttributeOverride(name = "name", column = @Column(name = "nm_ambiente"))
})
public class Ambiente extends CatalogEntity {

    @Column(name = "tp_local", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LocalEnum local;

    @OneToMany(mappedBy = "ambiente")
    private Set<ItemAmbiente> itemSet;

    public Ambiente(String name, LocalEnum localEnum) {
        super(name);
        this.local = localEnum;
    }

    @Override
    public List<SpecAssociationResDTO> getAssociations() {
        return itemSet.stream().map(rel -> new SpecAssociationResDTO(
                rel.getId(),
                rel.getItem().getId(),
                rel.getItem().getName(),
                rel.getItem().getIsActive(),
                rel.getIsRequired()
        )).collect(Collectors.toList());
    }

    @Override
    public Ambiente toEntity(SpecReqDTO specReqDTO) {
        return new Ambiente(
                specReqDTO.name(),
                specReqDTO.localEnum()
        );
    }
}