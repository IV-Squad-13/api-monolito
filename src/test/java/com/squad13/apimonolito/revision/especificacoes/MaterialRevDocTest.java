package com.squad13.apimonolito.revision.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import com.squad13.apimonolito.models.revision.mongo.MarcaRevDocElement;
import com.squad13.apimonolito.models.revision.mongo.MaterialRevDocElement;
import com.squad13.apimonolito.mongo.editor.MarcaDocElementRepository;
import com.squad13.apimonolito.mongo.editor.MaterialDocElementRepository;
import com.squad13.apimonolito.mongo.revision.MarcaRevDocElementRepository;
import com.squad13.apimonolito.mongo.revision.MaterialRevDocElementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class MaterialRevDocTest {

    @Autowired
    private MarcaDocElementRepository marcaDocRepository;

    @Autowired
    private MarcaRevDocElementRepository marcaRevDocRepository;

    @Autowired
    private MaterialDocElementRepository materialDocRepository;

    @Autowired
    private MaterialRevDocElementRepository materialRevDocRepository;

    private MarcaDocElement marca;
    private MaterialDocElement material;

    @BeforeEach
    void cleanDatabase() {
        materialRevDocRepository.deleteAll();
        marcaRevDocRepository.deleteAll();
        materialDocRepository.deleteAll();
        marcaDocRepository.deleteAll();

        marca = new MarcaDocElement();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        material = new MaterialDocElement();
        material.setName("Material");
        material.setCatalogId(1L);
        material.setInSync(true);
        material.setMarcaIds(List.of(marca.getId()));
        materialDocRepository.save(material);
    }

    @Test
    void testMaterialRevPersistence() {
        MarcaRevDocElement marcaRev = new MarcaRevDocElement();
        marcaRev.setRevisedDocId(marca.getId());
        marcaRev.setRevisionId(1L);
        marcaRev.setIsApproved(true);
        marcaRev.setComment("First revision");
        marcaRevDocRepository.save(marcaRev);

        MaterialRevDocElement materialRev = new MaterialRevDocElement();
        materialRev.setRevisedDocId(material.getId());
        materialRev.setRevisionId(1L);
        materialRevDocRepository.save(materialRev);

        MaterialRevDocElement foundRev = materialRevDocRepository.findByRevisedDocIdAndRevisionId(material.getId(), 1L)
                .orElse(null);
        assertThat(foundRev).isNotNull();
        assertThat(foundRev.getId()).isNotNull();
    }

    @Test
    void testMaterialRevUpdate() {
        MaterialRevDocElement materialRev = new MaterialRevDocElement();
        materialRev.setRevisedDocId(material.getId());
        materialRev.setRevisionId(1L);
        materialRevDocRepository.save(materialRev);

        MaterialRevDocElement found = materialRevDocRepository.findByRevisedDocIdAndRevisionId(material.getId(), 1L)
                .orElse(null);
        Assertions.assertNotNull(found);
        materialRevDocRepository.save(found);

        MaterialRevDocElement updated = materialRevDocRepository.findByRevisedDocIdAndRevisionId(material.getId(), 1L)
                .orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getRevisionId()).isEqualTo(1L);
    }

    @Test
    void testMaterialRevDelete() {
        MaterialRevDocElement materialRev = new MaterialRevDocElement();
        materialRev.setRevisedDocId(material.getId());
        materialRev.setRevisionId(1L);
        materialRevDocRepository.save(materialRev);

        materialRevDocRepository.deleteByRevisedDocIdAndRevisionId(material.getId(), 1L);
        MaterialRevDocElement found = materialRevDocRepository.findByRevisedDocIdAndRevisionId(material.getId(), 1L)
                .orElse(null);
        assertThat(found).isNull();
    }

    @Test
    void testMaterialRevDuplicateThrows() {
        MaterialRevDocElement rev1 = new MaterialRevDocElement();
        rev1.setRevisedDocId(material.getId());
        rev1.setRevisionId(1L);
        materialRevDocRepository.save(rev1);

        MaterialRevDocElement rev2 = new MaterialRevDocElement();
        rev2.setRevisedDocId(material.getId());
        rev2.setRevisionId(1L);

        assertThrows(DataIntegrityViolationException.class,
                () -> materialRevDocRepository.save(rev2));
    }
}