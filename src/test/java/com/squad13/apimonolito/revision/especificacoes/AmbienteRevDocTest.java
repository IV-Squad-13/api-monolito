package com.squad13.apimonolito.revision.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.models.revision.mongo.AmbienteRevDocElement;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDocElement;
import com.squad13.apimonolito.mongo.editor.AmbienteDocElementRepository;
import com.squad13.apimonolito.mongo.editor.ItemDocElementRepository;
import com.squad13.apimonolito.mongo.revision.AmbienteRevDocElementRepository;
import com.squad13.apimonolito.mongo.revision.ItemRevDocElementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AmbienteRevDocTest {

    @Autowired
    private ItemDocElementRepository itemDocRepository;

    @Autowired
    private ItemRevDocElementRepository itemRevDocRepository;

    @Autowired
    private AmbienteDocElementRepository ambienteDocRepository;

    @Autowired
    private AmbienteRevDocElementRepository ambienteRevDocRepository;

    private ItemDocElement item;
    private AmbienteDocElement ambiente;

    @BeforeEach
    void cleanDatabase() {
        ambienteRevDocRepository.deleteAll();
        itemRevDocRepository.deleteAll();
        ambienteDocRepository.deleteAll();
        itemDocRepository.deleteAll();

        item = new ItemDocElement();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        ambiente = new AmbienteDocElement();
        ambiente.setName("Ambiente");
        ambiente.setCatalogId(1L);
        ambiente.setInSync(true);
        ambiente.setItemIds(List.of(item.getId()));
        ambienteDocRepository.save(ambiente);
    }

    @Test
    void testAmbienteRevPersistence() {
        ItemRevDocElement itemRev = new ItemRevDocElement();
        itemRev.setItemDocId(item.getId());
        itemRev.setRevisionId(1L);
        itemRev.setApproved(true);
        itemRev.setComment("First revision");
        itemRevDocRepository.save(itemRev);

        AmbienteRevDocElement ambienteRev = new AmbienteRevDocElement();
        ambienteRev.setAmbienteDocId(ambiente.getId());
        ambienteRev.setRevisionId(1L);
        ambienteRevDocRepository.save(ambienteRev);

        AmbienteRevDocElement foundRev = ambienteRevDocRepository.findByAmbienteDocIdAndRevisionId(ambiente.getId(), 1L)
                .orElse(null);
        assertThat(foundRev).isNotNull();
        assertThat(foundRev.getId()).isNotNull();
    }

    @Test
    void testAmbienteRevUpdate() {
        AmbienteRevDocElement ambienteRev = new AmbienteRevDocElement();
        ambienteRev.setAmbienteDocId(ambiente.getId());
        ambienteRev.setRevisionId(1L);
        ambienteRevDocRepository.save(ambienteRev);

        AmbienteRevDocElement found = ambienteRevDocRepository.findByAmbienteDocIdAndRevisionId(ambiente.getId(), 1L)
                        .orElse(null);
        Assertions.assertNotNull(found);
        ambienteRevDocRepository.save(found);

        AmbienteRevDocElement updated = ambienteRevDocRepository.findByAmbienteDocIdAndRevisionId(ambiente.getId(), 1L)
                .orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getRevisionId()).isEqualTo(1L);
    }

    @Test
    void testAmbienteRevDelete() {
        AmbienteRevDocElement ambienteRev = new AmbienteRevDocElement();
        ambienteRev.setAmbienteDocId(ambiente.getId());
        ambienteRev.setRevisionId(1L);
        ambienteRevDocRepository.save(ambienteRev);

        ambienteRevDocRepository.deleteByAmbienteDocIdAndRevisionId(ambiente.getId(), 1L);
        AmbienteRevDocElement found = ambienteRevDocRepository.findByAmbienteDocIdAndRevisionId(ambiente.getId(), 1L)
                .orElse(null);
        assertThat(found).isNull();
    }

    @Test
    void testAmbienteRevDuplicateThrows() {
        AmbienteRevDocElement rev1 = new AmbienteRevDocElement();
        rev1.setAmbienteDocId(ambiente.getId());
        rev1.setRevisionId(1L);
        ambienteRevDocRepository.save(rev1);

        AmbienteRevDocElement rev2 = new AmbienteRevDocElement();
        rev2.setAmbienteDocId(ambiente.getId());
        rev2.setRevisionId(1L);

        assertThrows(DataIntegrityViolationException.class,
                () -> ambienteRevDocRepository.save(rev2));
    }
}