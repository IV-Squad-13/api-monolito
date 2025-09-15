package com.squad13.apimonolito.models.revision.structures;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
public abstract class ElementRevDoc {

    @Id
    private String id;

    private boolean isApproved;

    private String comment;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;
}