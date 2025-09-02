package com.squad13.apimonolito.util;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public abstract class AssociativeId implements Serializable {
    private Long associationA;
    private Long associationB;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssociativeId that)) return false;
        return Objects.equals(associationA, that.associationA) &&
                Objects.equals(associationB, that.associationB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(associationA, associationB);
    }
}