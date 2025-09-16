package com.squad13.apimonolito.editor.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.MarcaDoc;
import com.squad13.apimonolito.mongo.editor.MarcaDocRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MarcaDocTest {

    @Autowired
    private MarcaDocRepository marcaDocRepository;

    @BeforeEach
    void cleanDatabase() {
        marcaDocRepository.deleteAll();
    }

    @Test
    void testMarcaPersistence() {
        MarcaDoc marca = new MarcaDoc();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setInSync(true);

        marcaDocRepository.save(marca);

        assertThat(marca).isNotNull();
        assertThat(marca.getId()).isNotNull();
        assertThat(marca.getName()).isEqualTo("Marca");

        MarcaDoc foundMarca = marcaDocRepository.findByName("Marca");
        assertThat(foundMarca).isNotNull();
        assertThat(foundMarca.getId()).isEqualTo(marca.getId());
    }

    @Test
    void testMarcaUpdate() {
        MarcaDoc marca = new MarcaDoc();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        marca.setName("Marca1");
        marcaDocRepository.save(marca);

        MarcaDoc foundMarca = marcaDocRepository.findByName("Marca1");

        assertThat(foundMarca).isNotNull();
        assertThat(foundMarca.getUpdated()).isNotNull();
        assertThat(foundMarca.getName()).isEqualTo("Marca1");
    }

    @Test
    void testMarcaDelete() {
        marcaDocRepository.deleteAllByName("Marca");
        MarcaDoc foundMarca = marcaDocRepository.findByName("Marca");

        assertThat(foundMarca).isNull();
    }
}