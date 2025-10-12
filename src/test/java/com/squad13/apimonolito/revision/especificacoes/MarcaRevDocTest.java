package com.squad13.apimonolito.revision.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.MarcaDocElement;
import com.squad13.apimonolito.models.revision.mongo.MarcaRevDocElement;
import com.squad13.apimonolito.mongo.editor.MarcaDocElementRepository;
import com.squad13.apimonolito.mongo.revision.MarcaRevDocElementRepository;
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
        marcaRev.setMarca(marca);
        marcaRev.setRevisaoId(1L);
        marcaRev.setApproved(true);
        marcaRev.setComment("Revisão do marca");

        marcaRevDocRepository.save(marcaRev);

        MarcaRevDocElement foundMarcaRev = marcaRevDocRepository.findByMarcaAndRevisaoId(marca, 1L);
        assertThat(foundMarcaRev).isNotNull();
        assertThat(foundMarcaRev.getId()).isEqualTo(marcaRev.getId());
        assertThat(foundMarcaRev.getComment()).isEqualTo(marcaRev.getComment());
        assertThat(foundMarcaRev.getMarca().getName()).isEqualTo(marca.getName());
        assertThat(foundMarcaRev.isApproved()).isTrue();
    }

    @Test
    void testMarcaRevUpdate() {
        MarcaRevDocElement marcaRev = new MarcaRevDocElement();
        marcaRev.setMarca(marca);
        marcaRev.setRevisaoId(1L);
        marcaRev.setApproved(false);
        marcaRev.setComment("Revisão do marca");
        marcaRevDocRepository.save(marcaRev);

        MarcaRevDocElement found = marcaRevDocRepository.findByMarcaAndRevisaoId(marca, 1L);
        found.setApproved(true);
        found.setComment("Revisão Aprovada");
        marcaRevDocRepository.save(found);

        MarcaRevDocElement updated = marcaRevDocRepository.findByMarcaAndRevisaoId(marca, 1L);
        assertThat(updated.isApproved()).isTrue();
        assertThat(updated.getComment()).isEqualTo("Revisão Aprovada");
    }

    @Test
    void testMarcaRevDelete() {
        MarcaRevDocElement marcaRev = new MarcaRevDocElement();
        marcaRev.setMarca(marca);
        marcaRev.setRevisaoId(1L);
        marcaRev.setApproved(false);
        marcaRev.setComment("O marca deve ser removido");
        marcaRevDocRepository.save(marcaRev);

        marcaRevDocRepository.delete(marcaRev);
        MarcaRevDocElement found = marcaRevDocRepository.findByMarcaAndRevisaoId(marca, 1L);
        assertThat(found).isNull();
    }

    @Test
    void testDuplicateMarcaRevThrows() {
        MarcaRevDocElement rev1 = new MarcaRevDocElement();
        rev1.setMarca(marca);
        rev1.setRevisaoId(1L);
        rev1.setApproved(true);
        rev1.setComment("First revision");
        marcaRevDocRepository.save(rev1);

        MarcaRevDocElement rev2 = new MarcaRevDocElement();
        rev2.setMarca(marca);
        rev2.setRevisaoId(1L);
        rev2.setApproved(false);
        rev2.setComment("Duplicate revision");

        assertThrows(DataIntegrityViolationException.class,
                () -> marcaRevDocRepository.save(rev2));
    }
}