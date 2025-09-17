package com.squad13.apimonolito.editor.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.EmpreendimentoDoc;
import com.squad13.apimonolito.models.editor.mongo.MaterialDoc;
import com.squad13.apimonolito.models.editor.mongo.MarcaDoc;
import com.squad13.apimonolito.mongo.editor.EmpreendimentoDocRepository;
import com.squad13.apimonolito.mongo.editor.MaterialDocRepository;
import com.squad13.apimonolito.mongo.editor.MarcaDocRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MaterialDocTest {

    @Autowired
    private MarcaDocRepository marcaDocRepository;

    @Autowired
    private MaterialDocRepository materialDocRepository;

    @Autowired
    private EmpreendimentoDocRepository empDocRepository;

    private EmpreendimentoDoc empDoc;


    @BeforeEach
    void cleanDatabase() {
        marcaDocRepository.deleteAll();
        materialDocRepository.deleteAll();
        empDocRepository.deleteAll();

        empDoc = new EmpreendimentoDoc();
        empDoc.setName("Doc Empreendimento B");
        empDoc.setEmpreendimentoId(1L);
        empDoc.setDesc("Descricao B");
        empDoc.setObs("Observacao B");
        empDocRepository.save(empDoc);
    }

    @Test
    void testMaterialPersistence() {
        MarcaDoc marca = new MarcaDoc();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setEmpreendimentoDoc(empDoc);
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        MaterialDoc material = new MaterialDoc();
        material.setName("Material");
        material.setCatalogId(1L);
        material.setEmpreendimentoDoc(empDoc);
        material.setInSync(true);
        material.setMarcaDocList(List.of(marca));
        materialDocRepository.save(material);

        assertThat(material).isNotNull();
        assertThat(material.getId()).isNotNull();
        assertThat(material.getName()).isEqualTo("Material");

        MaterialDoc foundMaterial = materialDocRepository.findByName("Material");
        assertThat(foundMaterial).isNotNull();
        assertThat(foundMaterial.getId()).isEqualTo(material.getId());
        assertThat(foundMaterial.getMarcaDocList().getFirst().getName()).isEqualTo("Marca");
    }

    @Test
    void testMaterialUpdate() {
        MarcaDoc marca = new MarcaDoc();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setEmpreendimentoDoc(empDoc);
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        MaterialDoc material = new MaterialDoc();
        material.setName("Material");
        material.setCatalogId(1L);
        material.setEmpreendimentoDoc(empDoc);
        material.setInSync(true);
        material.setMarcaDocList(List.of(marca));
        materialDocRepository.save(material);

        MaterialDoc foundMaterial = materialDocRepository.findByName("Material");
        foundMaterial.setName("Material2");
        materialDocRepository.save(foundMaterial);

        MaterialDoc updated = materialDocRepository.findByName("Material2");
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Material2");
    }

    @Test
    void testMaterialDelete() {
        materialDocRepository.deleteAllByName("Material");
        MaterialDoc foundMaterial = materialDocRepository.findByName("Material");

        assertThat(foundMaterial).isNull();
    }
}