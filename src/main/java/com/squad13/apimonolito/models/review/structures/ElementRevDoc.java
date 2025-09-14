package com.squad13.apimonolito.models.review.structures;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

public abstract class ElementRevDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private boolean isApproved;

    private String comment;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;
}