package com.squad13.apimonolito.util.enums;

import com.squad13.apimonolito.models.catalog.Ambiente;
import com.squad13.apimonolito.models.catalog.ItemDesc;
import com.squad13.apimonolito.models.catalog.Marca;
import com.squad13.apimonolito.models.catalog.Material;
import com.squad13.apimonolito.models.catalog.associative.ItemAmbiente;
import com.squad13.apimonolito.models.catalog.associative.MarcaMaterial;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.util.mappers.EditorMapper;
import lombok.Getter;

@Getter
public enum CatalogRelationEnum {
    ITEM_AMBIENTE(
            ItemAmbiente.class,
            "ambiente",
            "itemDesc",
            (editor, espec, source) -> editor.fromCatalog(espec, (Ambiente) source),
            (editor, espec, target) -> editor.fromCatalog(espec, (ItemDesc) target)
    ),
    MARCA_MATERIAL(
            MarcaMaterial.class,
            "marca",
            "material",
            (editor, espec, source) -> editor.fromCatalog(espec, (Marca) source),
            (editor, espec, target) -> editor.fromCatalog(espec, (Material) target)
    );

    private final Class<?> relationClass;
    private final String sourceField;
    private final String targetField;
    private final TriFunction<EditorMapper, EspecificacaoDoc, Object, Object> sourceMapper;
    private final TriFunction<EditorMapper, EspecificacaoDoc, Object, Object> targetMapper;

    CatalogRelationEnum(
            Class<?> relationClass,
            String sourceField,
            String targetField,
            TriFunction<EditorMapper, EspecificacaoDoc, Object, Object> sourceMapper,
            TriFunction<EditorMapper, EspecificacaoDoc, Object, Object> targetMapper
    ) {
        this.relationClass = relationClass;
        this.sourceField = sourceField;
        this.targetField = targetField;
        this.sourceMapper = sourceMapper;
        this.targetMapper = targetMapper;
    }

    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }
}