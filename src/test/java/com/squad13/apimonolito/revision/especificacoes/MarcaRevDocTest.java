package com.squad13.apimonolito.revision.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.models.revision.mongo.MarcaRevDocElement;
import com.squad13.apimonolito.mongo.editor.MarcaDocElementRepository;
import com.squad13.apimonolito.mongo.revision.MarcaRevDocElementRepository;
import org.junit.jupiter.api.Assertions;
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
    private MarcaDocElementRepository marcaDocRepository;

    @Autowired
    private MarcaRevDocElementRepository marcaRevDocRepository;

    private MarcaDocElement marca;

    @BeforeEach
    void setup() {
        marcaRevDocRepository.deleteAll();
        marcaDocRepository.deleteAll();

        marca = new MarcaDocElement();
        marca.setName("Marca");
        marca.setCatalogId(1L);
        marca.setInSync(true);
        marcaDocRepository.save(marca);

        assertThat(marcaDocRepository.findAll()).hasSize(1);
    }

    @Test
    void testMarcaRevPersistence() {
        MarcaRevDocElement marcaRev = new MarcaRevDocElement();
        marcaRev.setMarcaDocId(marca.getId());
        marcaRev.setRevisionId(1L);
        marcaRev.setApproved(true);
        marcaRev.setComment("Revisão do marca");

        marcaRevDocRepository.save(marcaRev);

        MarcaRevDocElement foundMarcaRev = marcaRevDocRepository.findByMarcaDocIdAndRevisionId(marca.getId(), 1L)
                .orElse(null);
        assertThat(foundMarcaRev).isNotNull();
        assertThat(foundMarcaRev.getId()).isEqualTo(marcaRev.getId());
        assertThat(foundMarcaRev.getComment()).isEqualTo(marcaRev.getComment());
        assertThat(foundMarcaRev.getMarcaDocId()).isEqualTo(marca.getId());
        assertThat(foundMarcaRev.isApproved()).isTrue();
    }

    @Test
    void testMarcaRevUpdate() {
        MarcaRevDocElement marcaRev = new MarcaRevDocElement();
        marcaRev.setMarcaDocId(marca.getId());
        marcaRev.setRevisionId(1L);
        marcaRev.setApproved(false);
        marcaRev.setComment("Revisão do marca");
        marcaRevDocRepository.save(marcaRev);

        MarcaRevDocElement found = marcaRevDocRepository.findByMarcaDocIdAndRevisionId(marca.getId(), 1L)
                .orElse(null);
        Assertions.assertNotNull(found);
        found.setApproved(true);
        found.setComment("Revisão Aprovada");
        marcaRevDocRepository.save(found);

        MarcaRevDocElement updated = marcaRevDocRepository.findByMarcaDocIdAndRevisionId(marca.getId(), 1L)
                .orElse(null);
        Assertions.assertNotNull(updated);
        assertThat(updated.isApproved()).isTrue();
        assertThat(updated.getComment()).isEqualTo("Revisão Aprovada");
    }

    @Test
    void testMarcaRevDelete() {
        MarcaRevDocElement marcaRev = new MarcaRevDocElement();
        marcaRev.setMarcaDocId(marca.getId());
        marcaRev.setRevisionId(1L);
        marcaRev.setApproved(false);
        marcaRev.setComment("O marca deve ser removido");
        marcaRevDocRepository.save(marcaRev);

        marcaRevDocRepository.delete(marcaRev);
        MarcaRevDocElement found = marcaRevDocRepository.findByMarcaDocIdAndRevisionId(marca.getId(), 1L)
                .orElse(null);
        assertThat(found).isNull();
    }

    @Test
    void testDuplicateMarcaRevThrows() {
        MarcaRevDocElement rev1 = new MarcaRevDocElement();
        rev1.setMarcaDocId(marca.getId());
        rev1.setRevisionId(1L);
        rev1.setApproved(true);
        rev1.setComment("First revision");
        marcaRevDocRepository.save(rev1);

        MarcaRevDocElement rev2 = new MarcaRevDocElement();
        rev2.setMarcaDocId(marca.getId());
        rev2.setRevisionId(1L);
        rev2.setApproved(false);
        rev2.setComment("Duplicate revision");

        assertThrows(DataIntegrityViolationException.class,
                () -> marcaRevDocRepository.save(rev2));
    }
}