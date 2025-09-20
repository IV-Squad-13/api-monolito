package com.squad13.apimonolito.revision.especificacoes;

import com.mongodb.DuplicateKeyException;
import com.squad13.apimonolito.models.editor.mongo.MaterialDoc;
import com.squad13.apimonolito.models.editor.mongo.MarcaDoc;
import com.squad13.apimonolito.models.revision.mongo.MaterialRevDoc;
import com.squad13.apimonolito.models.revision.mongo.MarcaRevDoc;
import com.squad13.apimonolito.mongo.editor.MaterialDocRepository;
import com.squad13.apimonolito.mongo.editor.MarcaDocRepository;
import com.squad13.apimonolito.mongo.revision.MaterialRevDocRepository;
import com.squad13.apimonolito.mongo.revision.MarcaRevDocRepository;
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
    private MarcaDocRepository marcaDocRepository;

    @Autowired
    private MarcaRevDocRepository marcaRevDocRepository;

    @Autowired
    private MaterialDocRepository materialDocRepository;

    @Autowired
    private MaterialRevDocRepository materialRevDocRepository;

    private MarcaDoc marca;
    private MaterialDoc material;

    @BeforeEach
    void cleanDatabase() {
        materialRevDocRepository.deleteAll();
        marcaRevDocRepository.deleteAll();
        materialDocRepository.deleteAll();
        marcaDocRepository.deleteAll();

        marca = new MarcaDoc();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        material = new MaterialDoc();
        material.setName("Material");
        material.setCatalogId(1L);
        material.setInSync(true);
        material.setMarcaDocList(List.of(marca));
        materialDocRepository.save(material);
    }

    @Test
    void testMaterialRevPersistence() {
        MarcaRevDoc marcaRev = new MarcaRevDoc();
        marcaRev.setMarca(marca);
        marcaRev.setRevisaoId(1L);
        marcaRev.setApproved(true);
        marcaRev.setComment("First revision");
        marcaRevDocRepository.save(marcaRev);

        MaterialRevDoc materialRev = new MaterialRevDoc();
        materialRev.setMaterial(material);
        materialRev.setRevisaoId(1L);
        materialRev.setMarcaRevList(List.of(marcaRev));
        materialRevDocRepository.save(materialRev);

        MaterialRevDoc foundRev = materialRevDocRepository.findByMaterialAndRevisaoId(material, 1L);
        assertThat(foundRev).isNotNull();
        assertThat(foundRev.getId()).isNotNull();
        assertThat(foundRev.getMarcaRevList()).hasSize(1);
        assertThat(foundRev.getMarcaRevList().getFirst().getMarca().getName()).isEqualTo("Marca");
    }

    @Test
    void testMaterialRevUpdate() {
        MaterialRevDoc materialRev = new MaterialRevDoc();
        materialRev.setMaterial(material);
        materialRev.setRevisaoId(1L);
        materialRev.setMarcaRevList(List.of());
        materialRevDocRepository.save(materialRev);

        MaterialRevDoc found = materialRevDocRepository.findByMaterialAndRevisaoId(material, 1L);
        found.setMarcaRevList(List.of());
        materialRevDocRepository.save(found);

        MaterialRevDoc updated = materialRevDocRepository.findByMaterialAndRevisaoId(material, 1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getRevisaoId()).isEqualTo(1L);
    }

    @Test
    void testMaterialRevDelete() {
        MaterialRevDoc materialRev = new MaterialRevDoc();
        materialRev.setMaterial(material);
        materialRev.setRevisaoId(1L);
        materialRevDocRepository.save(materialRev);

        materialRevDocRepository.deleteByMaterialAndRevisaoId(material, 1L);
        MaterialRevDoc found = materialRevDocRepository.findByMaterialAndRevisaoId(material, 1L);
        assertThat(found).isNull();
    }

    @Test
    void testMaterialRevDuplicateThrows() {
        MaterialRevDoc rev1 = new MaterialRevDoc();
        rev1.setMaterial(material);
        rev1.setRevisaoId(1L);
        rev1.setMarcaRevList(List.of());
        materialRevDocRepository.save(rev1);

        MaterialRevDoc rev2 = new MaterialRevDoc();
        rev2.setMaterial(material);
        rev2.setRevisaoId(1L);
        rev2.setMarcaRevList(List.of());

        assertThrows(DataIntegrityViolationException.class,
                () -> materialRevDocRepository.save(rev2));
    }
}