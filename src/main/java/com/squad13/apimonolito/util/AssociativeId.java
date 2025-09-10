package com.squad13.apimonolito.util;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public abstract class AssociativeId implements Serializable {
    private Long relA;
    private Long relB;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssociativeId that)) return false;
        return Objects.equals(relA, that.relA) &&
                Objects.equals(relB, that.relB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relA, relB);
    }
}