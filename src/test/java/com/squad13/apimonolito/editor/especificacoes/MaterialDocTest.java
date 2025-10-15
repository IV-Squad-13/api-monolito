package com.squad13.apimonolito.editor.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.MaterialDocElement;
import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.editor.MaterialDocElementRepository;
import com.squad13.apimonolito.mongo.editor.MarcaDocElementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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
        marca.setEspecificacaoDoc(empDoc);
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        MaterialDocElement material = new MaterialDocElement();
        material.setName("Material");
        material.setCatalogId(1L);
        material.setEspecificacaoDoc(empDoc);
        material.setInSync(true);
        material.setMarcaDocList(List.of(marca));
        materialDocRepository.save(material);

        assertThat(material).isNotNull();
        assertThat(material.getId()).isNotNull();
        assertThat(material.getName()).isEqualTo("Material");

        MaterialDocElement foundMaterial = materialDocRepository.findByName("Material").orElse(null);
        assertThat(foundMaterial).isNotNull();
        assertThat(foundMaterial.getId()).isEqualTo(material.getId());
        assertThat(foundMaterial.getMarcaDocList().getFirst().getName()).isEqualTo("Marca");
    }

    @Test
    void testMaterialUpdate() {
        MarcaDocElement marca = new MarcaDocElement();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setEspecificacaoDoc(empDoc);
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        MaterialDocElement material = new MaterialDocElement();
        material.setName("Material");
        material.setCatalogId(1L);
        material.setEspecificacaoDoc(empDoc);
        material.setInSync(true);
        material.setMarcaDocList(List.of(marca));
        materialDocRepository.save(material);

        MaterialDocElement foundMaterial = materialDocRepository.findByName("Material").orElse(null);
        foundMaterial.setName("Material2");
        materialDocRepository.save(foundMaterial);

        MaterialDocElement updated = materialDocRepository.findByName("Material2").orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Material2");
    }

    @Test
    void testMaterialDelete() {
        materialDocRepository.deleteAllByName("Material");
        MaterialDocElement foundMaterial = materialDocRepository.findByName("Material").orElse(null);

        assertThat(foundMaterial).isNull();
    }
}