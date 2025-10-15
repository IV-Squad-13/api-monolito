package com.squad13.apimonolito.editor.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.editor.MarcaDocElementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DataMongoTest
class MarcaDocTest {

    @Autowired
    private MarcaDocElementRepository marcaDocRepository;

    @Autowired
    private EspecificacaoDocRepository empDocRepository;

    private EspecificacaoDoc empDoc;

    @BeforeEach
    void cleanDatabase() {
        marcaDocRepository.deleteAll();
        empDocRepository.deleteAll();

        empDoc = new EspecificacaoDoc();
        empDoc.setName("Doc Empreendimento B");
        empDoc.setEmpreendimentoId(1L);
        empDoc.setDesc("Descricao B");
        empDoc.setObs("Observacao B");
        empDocRepository.save(empDoc);
    }

    @Test
    void testMarcaPersistence() {
        MarcaDocElement marca = new MarcaDocElement();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setEspecificacaoDoc(empDoc);
        marca.setInSync(true);

        marcaDocRepository.save(marca);

        assertThat(marca).isNotNull();
        assertThat(marca.getId()).isNotNull();
        assertThat(marca.getName()).isEqualTo("Marca");

        MarcaDocElement foundMarca = marcaDocRepository.findByName("Marca").orElse(null);;
        assertThat(foundMarca).isNotNull();
        assertThat(foundMarca.getId()).isEqualTo(marca.getId());
    }

    @Test
    void testMarcaUpdate() {
        MarcaDocElement marca = new MarcaDocElement();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setEspecificacaoDoc(empDoc);
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        marca.setName("Marca1");
        marcaDocRepository.save(marca);

        MarcaDocElement foundMarca = marcaDocRepository.findByName("Marca1").orElse(null);;

        assertThat(foundMarca).isNotNull();
        assertThat(foundMarca.getUpdated()).isNotNull();
        assertThat(foundMarca.getName()).isEqualTo("Marca1");
    }

    @Test
    void testMarcaDelete() {
        marcaDocRepository.deleteAllByName("Marca");
        MarcaDocElement foundMarca = marcaDocRepository.findByName("Marca").orElse(null);;

        assertThat(foundMarca).isNull();
    }
}