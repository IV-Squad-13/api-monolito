package com.squad13.apimonolito.editor.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.editor.MarcaDocElementRepository;
import com.squad13.apimonolito.mongo.editor.MaterialDocElementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DataMongoTest
class MaterialDocTest {

    @Autowired
    private MarcaDocElementRepository marcaDocRepository;

    @Autowired
    private MaterialDocElementRepository materialDocRepository;

    @Autowired
    private EspecificacaoDocRepository empDocRepository;

    private EspecificacaoDoc empDoc;


    @BeforeEach
    void cleanDatabase() {
        marcaDocRepository.deleteAll();
        materialDocRepository.deleteAll();
        empDocRepository.deleteAll();

        empDoc = new EspecificacaoDoc();
        empDoc.setName("Doc Empreendimento B");
        empDoc.setEmpreendimentoId(1L);
        empDoc.setDesc("Descricao B");
        empDoc.setObs("Observacao B");
        empDocRepository.save(empDoc);
    }

    @Test
    void testMaterialPersistence() {
        MarcaDocElement marca = new MarcaDocElement();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setEspecificacaoId(empDoc.getId());
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        MaterialDocElement material = new MaterialDocElement();
        material.setName("Material");
        material.setCatalogId(1L);
        material.setEspecificacaoId(empDoc.getId());
        material.setInSync(true);
        material.setMarcaIds(List.of(marca.getId()));
        materialDocRepository.save(material);

        assertThat(material).isNotNull();
        assertThat(material.getId()).isNotNull();
        assertThat(material.getName()).isEqualTo("Material");

        MaterialDocElement foundMaterial = materialDocRepository.findByName("Material").orElse(null);
        ;
        assertThat(foundMaterial).isNotNull();
        assertThat(foundMaterial.getId()).isEqualTo(material.getId());
        assertThat(foundMaterial.getMarcaIds().getFirst()).isEqualTo(marca.getId());
    }

    @Test
    void testMaterialUpdate() {
        MarcaDocElement marca = new MarcaDocElement();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setEspecificacaoId(empDoc.getId());
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        MaterialDocElement material = new MaterialDocElement();
        material.setName("Material");
        material.setCatalogId(1L);
        material.setEspecificacaoId(empDoc.getId());
        material.setInSync(true);
        material.setMarcaIds(List.of(marca.getId()));
        materialDocRepository.save(material);

        MaterialDocElement foundMaterial = materialDocRepository.findByName("Material").orElse(null);
        ;
        Assertions.assertNotNull(foundMaterial);
        foundMaterial.setName("Material2");
        materialDocRepository.save(foundMaterial);

        MaterialDocElement updated = materialDocRepository.findByName("Material2").orElse(null);
        ;
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Material2");
    }

    @Test
    void testMaterialDelete() {
        materialDocRepository.deleteAllByName("Material");
        MaterialDocElement foundMaterial = materialDocRepository.findByName("Material").orElse(null);
        ;

        assertThat(foundMaterial).isNull();
    }
}