package com.squad13.apimonolito.revision.especificacoes;

import com.mongodb.DuplicateKeyException;
import com.squad13.apimonolito.models.editor.mongo.AmbienteDoc;
import com.squad13.apimonolito.models.editor.mongo.ItemDoc;
import com.squad13.apimonolito.models.revision.mongo.AmbienteRevDoc;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDoc;
import com.squad13.apimonolito.mongo.editor.AmbienteDocRepository;
import com.squad13.apimonolito.mongo.editor.ItemDocRepository;
import com.squad13.apimonolito.mongo.revision.AmbienteRevDocRepository;
import com.squad13.apimonolito.mongo.revision.ItemRevDocRepository;
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
    private ItemDocRepository itemDocRepository;

    @Autowired
    private ItemRevDocRepository itemRevDocRepository;

    @Autowired
    private AmbienteDocRepository ambienteDocRepository;

    @Autowired
    private AmbienteRevDocRepository ambienteRevDocRepository;

    private ItemDoc item;
    private AmbienteDoc ambiente;

    @BeforeEach
    void cleanDatabase() {
        ambienteRevDocRepository.deleteAll();
        itemRevDocRepository.deleteAll();
        ambienteDocRepository.deleteAll();
        itemDocRepository.deleteAll();

        item = new ItemDoc();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        ambiente = new AmbienteDoc();
        ambiente.setName("Ambiente");
        ambiente.setCatalogId(1L);
        ambiente.setInSync(true);
        ambiente.setItemDocList(List.of(item));
        ambienteDocRepository.save(ambiente);
    }

    @Test
    void testAmbienteRevPersistence() {
        ItemRevDoc itemRev = new ItemRevDoc();
        itemRev.setItem(item);
        itemRev.setRevisaoId(1L);
        itemRev.setApproved(true);
        itemRev.setComment("First revision");
        itemRevDocRepository.save(itemRev);

        AmbienteRevDoc ambienteRev = new AmbienteRevDoc();
        ambienteRev.setAmbiente(ambiente);
        ambienteRev.setRevisaoId(1L);
        ambienteRev.setItemRevList(List.of(itemRev));
        ambienteRevDocRepository.save(ambienteRev);

        AmbienteRevDoc foundRev = ambienteRevDocRepository.findByAmbienteAndRevisaoId(ambiente, 1L);
        assertThat(foundRev).isNotNull();
        assertThat(foundRev.getId()).isNotNull();
        assertThat(foundRev.getItemRevList()).hasSize(1);
        assertThat(foundRev.getItemRevList().get(0).getItem().getName()).isEqualTo("Item");
    }

    @Test
    void testAmbienteRevUpdate() {
        AmbienteRevDoc ambienteRev = new AmbienteRevDoc();
        ambienteRev.setAmbiente(ambiente);
        ambienteRev.setRevisaoId(1L);
        ambienteRev.setItemRevList(List.of());
        ambienteRevDocRepository.save(ambienteRev);

        AmbienteRevDoc found = ambienteRevDocRepository.findByAmbienteAndRevisaoId(ambiente, 1L);
        found.setItemRevList(List.of());
        ambienteRevDocRepository.save(found);

        AmbienteRevDoc updated = ambienteRevDocRepository.findByAmbienteAndRevisaoId(ambiente, 1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getRevisaoId()).isEqualTo(1L);
    }

    @Test
    void testAmbienteRevDelete() {
        AmbienteRevDoc ambienteRev = new AmbienteRevDoc();
        ambienteRev.setAmbiente(ambiente);
        ambienteRev.setRevisaoId(1L);
        ambienteRevDocRepository.save(ambienteRev);

        ambienteRevDocRepository.deleteByAmbienteAndRevisaoId(ambiente, 1L);
        AmbienteRevDoc found = ambienteRevDocRepository.findByAmbienteAndRevisaoId(ambiente, 1L);
        assertThat(found).isNull();
    }

    @Test
    void testAmbienteRevDuplicateThrows() {
        AmbienteRevDoc rev1 = new AmbienteRevDoc();
        rev1.setAmbiente(ambiente);
        rev1.setRevisaoId(1L);
        rev1.setItemRevList(List.of());
        ambienteRevDocRepository.save(rev1);

        AmbienteRevDoc rev2 = new AmbienteRevDoc();
        rev2.setAmbiente(ambiente);
        rev2.setRevisaoId(1L);
        rev2.setItemRevList(List.of());

        assertThrows(DataIntegrityViolationException.class,
                () -> ambienteRevDocRepository.save(rev2));
    }
}