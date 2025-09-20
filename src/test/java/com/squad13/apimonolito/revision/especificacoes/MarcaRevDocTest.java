package com.squad13.apimonolito.revision.especificacoes;

import com.mongodb.DuplicateKeyException;
import com.squad13.apimonolito.models.editor.mongo.MarcaDoc;
import com.squad13.apimonolito.models.revision.mongo.MarcaRevDoc;
import com.squad13.apimonolito.mongo.editor.MarcaDocRepository;
import com.squad13.apimonolito.mongo.revision.MarcaRevDocRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class MarcaRevDocTest {

    @Autowired
    private MarcaDocRepository marcaDocRepository;

    @Autowired
    private MarcaRevDocRepository marcaRevDocRepository;

    private MarcaDoc marca;

    @BeforeEach
    void setup() {
        marcaRevDocRepository.deleteAll();
        marcaDocRepository.deleteAll();

        marca = new MarcaDoc();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        assertThat(marcaDocRepository.findAll()).hasSize(1);
    }

    @Test
    void testMarcaRevPersistence() {
        MarcaRevDoc marcaRev = new MarcaRevDoc();
        marcaRev.setMarca(marca);
        marcaRev.setRevisaoId(1L);
        marcaRev.setApproved(true);
        marcaRev.setComment("Revisão do marca");

        marcaRevDocRepository.save(marcaRev);

        MarcaRevDoc foundMarcaRev = marcaRevDocRepository.findByMarcaAndRevisaoId(marca, 1L);
        assertThat(foundMarcaRev).isNotNull();
        assertThat(foundMarcaRev.getId()).isEqualTo(marcaRev.getId());
        assertThat(foundMarcaRev.getComment()).isEqualTo(marcaRev.getComment());
        assertThat(foundMarcaRev.getMarca().getName()).isEqualTo(marca.getName());
        assertThat(foundMarcaRev.isApproved()).isTrue();
    }

    @Test
    void testMarcaRevUpdate() {
        MarcaRevDoc marcaRev = new MarcaRevDoc();
        marcaRev.setMarca(marca);
        marcaRev.setRevisaoId(1L);
        marcaRev.setApproved(false);
        marcaRev.setComment("Revisão do marca");
        marcaRevDocRepository.save(marcaRev);

        MarcaRevDoc found = marcaRevDocRepository.findByMarcaAndRevisaoId(marca, 1L);
        found.setApproved(true);
        found.setComment("Revisão Aprovada");
        marcaRevDocRepository.save(found);

        MarcaRevDoc updated = marcaRevDocRepository.findByMarcaAndRevisaoId(marca, 1L);
        assertThat(updated.isApproved()).isTrue();
        assertThat(updated.getComment()).isEqualTo("Revisão Aprovada");
    }

    @Test
    void testMarcaRevDelete() {
        MarcaRevDoc marcaRev = new MarcaRevDoc();
        marcaRev.setMarca(marca);
        marcaRev.setRevisaoId(1L);
        marcaRev.setApproved(false);
        marcaRev.setComment("O marca deve ser removido");
        marcaRevDocRepository.save(marcaRev);

        marcaRevDocRepository.delete(marcaRev);
        MarcaRevDoc found = marcaRevDocRepository.findByMarcaAndRevisaoId(marca, 1L);
        assertThat(found).isNull();
    }

    @Test
    void testDuplicateMarcaRevThrows() {
        MarcaRevDoc rev1 = new MarcaRevDoc();
        rev1.setMarca(marca);
        rev1.setRevisaoId(1L);
        rev1.setApproved(true);
        rev1.setComment("First revision");
        marcaRevDocRepository.save(rev1);

        MarcaRevDoc rev2 = new MarcaRevDoc();
        rev2.setMarca(marca);
        rev2.setRevisaoId(1L);
        rev2.setApproved(false);
        rev2.setComment("Duplicate revision");

        assertThrows(DataIntegrityViolationException.class,
                () -> marcaRevDocRepository.save(rev2));
    }
}