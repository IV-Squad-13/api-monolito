package com.squad13.apimonolito.mongo.review;

import com.squad13.apimonolito.models.review.mongo.MarcaRevDoc;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRevDocRepository extends ElementRevDocRepository<MarcaRevDoc> { }