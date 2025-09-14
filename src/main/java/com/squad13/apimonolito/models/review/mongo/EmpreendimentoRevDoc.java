package com.squad13.apimonolito.models.review.mongo;

import com.squad13.apimonolito.models.editor.mongo.EmpreendimentoDoc;
import com.squad13.apimonolito.models.review.structures.ElementRevDoc;
import com.squad13.apimonolito.util.annotations.MongoEntityType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@MongoEntityType("EMPREENDIMENTO")
@Document(collection = "empreendimento_rev")
public class EmpreendimentoRevDoc extends ElementRevDoc {

    @DBRef
    @Field("empreendimento")
    private EmpreendimentoDoc empreendimentoDoc;

    @DBRef
    @Field("locais")
    private List<LocalRevDoc> localRevDocList;

    @DBRef
    @Field("materiais")
    private List<MaterialRevDoc> materialRevList;

    private boolean isNameApproved;
    private boolean isDescApproved;
    private boolean isObsApproved;
}