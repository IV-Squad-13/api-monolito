package com.squad13.apimonolito.revision.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.models.revision.mongo.MaterialRevDocElement;
import com.squad13.apimonolito.models.revision.mongo.MarcaRevDocElement;
import com.squad13.apimonolito.mongo.editor.MaterialDocElementRepository;
import com.squad13.apimonolito.mongo.editor.MarcaDocElementRepository;
import com.squad13.apimonolito.mongo.revision.MaterialRevDocElementRepository;
import com.squad13.apimonolito.mongo.revision.MarcaRevDocElementRepository;
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
        marcaRev.setMarca(marca);
        marcaRev.setRevisaoId(1L);
        marcaRev.setApproved(true);
        marcaRev.setComment("First revision");
        marcaRevDocRepository.save(marcaRev);

        MaterialRevDocElement materialRev = new MaterialRevDocElement();
        materialRev.setMaterial(material);
        materialRev.setRevisaoId(1L);
        materialRev.setMarcaRevList(List.of(marcaRev));
        materialRevDocRepository.save(materialRev);

        MaterialRevDocElement foundRev = materialRevDocRepository.findByMaterialAndRevisaoId(material, 1L);
        assertThat(foundRev).isNotNull();
        assertThat(foundRev.getId()).isNotNull();
        assertThat(foundRev.getMarcaRevList()).hasSize(1);
        assertThat(foundRev.getMarcaRevList().getFirst().getMarca().getName()).isEqualTo("Marca");
    }

    @Test
    void testMaterialRevUpdate() {
        MaterialRevDocElement materialRev = new MaterialRevDocElement();
        materialRev.setMaterial(material);
        materialRev.setRevisaoId(1L);
        materialRev.setMarcaRevList(List.of());
        materialRevDocRepository.save(materialRev);

        MaterialRevDocElement found = materialRevDocRepository.findByMaterialAndRevisaoId(material, 1L);
        found.setMarcaRevList(List.of());
        materialRevDocRepository.save(found);

        MaterialRevDocElement updated = materialRevDocRepository.findByMaterialAndRevisaoId(material, 1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getRevisaoId()).isEqualTo(1L);
    }

    @Test
    void testMaterialRevDelete() {
        MaterialRevDocElement materialRev = new MaterialRevDocElement();
        materialRev.setMaterial(material);
        materialRev.setRevisaoId(1L);
        materialRevDocRepository.save(materialRev);

        materialRevDocRepository.deleteByMaterialAndRevisaoId(material, 1L);
        MaterialRevDocElement found = materialRevDocRepository.findByMaterialAndRevisaoId(material, 1L);
        assertThat(found).isNull();
    }

    @Test
    void testMaterialRevDuplicateThrows() {
        MaterialRevDocElement rev1 = new MaterialRevDocElement();
        rev1.setMaterial(material);
        rev1.setRevisaoId(1L);
        rev1.setMarcaRevList(List.of());
        materialRevDocRepository.save(rev1);

        MaterialRevDocElement rev2 = new MaterialRevDocElement();
        rev2.setMaterial(material);
        rev2.setRevisaoId(1L);
        rev2.setMarcaRevList(List.of());

        assertThrows(DataIntegrityViolationException.class,
                () -> materialRevDocRepository.save(rev2));
    }
}