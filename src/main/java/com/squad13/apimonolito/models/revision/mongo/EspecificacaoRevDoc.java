package com.squad13.apimonolito.models.revision.mongo;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.revision.structures.ElementRevDoc;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "empreendimento_rev")
@CompoundIndex(name = "catalog_name_unique", def = "{'revisaoId' : 1, 'empreendimento': 1}", unique = true)
public class EspecificacaoRevDoc extends ElementRevDoc {

    @DBRef
    @Field("empreendimento")
    private EspecificacaoDoc empreendimento;

    @DBRef
    @Field("locais")
    private List<LocalRevDoc> localRevList;

    @DBRef
    @Field("materiais")
    private List<MaterialRevDoc> materialRevList;

    private boolean isNameApproved;
    private boolean isDescApproved;
    private boolean isObsApproved;
}