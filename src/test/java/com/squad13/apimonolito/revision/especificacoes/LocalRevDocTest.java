package com.squad13.apimonolito.revision.especificacoes;

import com.mongodb.DuplicateKeyException;
import com.squad13.apimonolito.models.editor.mongo.AmbienteDoc;
import com.squad13.apimonolito.models.editor.mongo.ItemDoc;
import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.models.revision.mongo.AmbienteRevDoc;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDoc;
import com.squad13.apimonolito.models.revision.mongo.LocalRevDoc;
import com.squad13.apimonolito.mongo.editor.AmbienteDocRepository;
import com.squad13.apimonolito.mongo.editor.ItemDocRepository;
import com.squad13.apimonolito.mongo.editor.LocalDocRepository;
import com.squad13.apimonolito.mongo.revision.AmbienteRevDocRepository;
import com.squad13.apimonolito.mongo.revision.ItemRevDocRepository;
import com.squad13.apimonolito.mongo.revision.LocalRevDocRepository;
import com.squad13.apimonolito.util.enums.LocalEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class LocalRevDocTest {

    @Autowired
    private ItemDocRepository itemDocRepository;

    @Autowired
    private ItemRevDocRepository itemRevDocRepository;

    @Autowired
    private AmbienteDocRepository ambienteDocRepository;

    @Autowired
    private AmbienteRevDocRepository ambienteRevDocRepository;

    @Autowired
    private LocalDocRepository localDocRepository;

    @Autowired
    private LocalRevDocRepository localRevDocRepository;

    private ItemDoc item;
    private AmbienteDoc ambiente;
    private LocalDoc local;

    @BeforeEach
    void cleanDatabase() {
        localRevDocRepository.deleteAll();
        ambienteRevDocRepository.deleteAll();
        itemRevDocRepository.deleteAll();
        localDocRepository.deleteAll();
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

        local = new LocalDoc();
        local.setLocal(LocalEnum.AREA_COMUM);
        local.setCatalogId(1L);
        local.setInSync(true);
        local.setName("Local Test");
        local.setAmbienteDocList(List.of(ambiente));
        localDocRepository.save(local);
    }

    @Test
    void testLocalRevPersistence() {
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

        LocalRevDoc localRev = new LocalRevDoc();
        localRev.setLocal(local);
        localRev.setRevisaoId(1L);
        localRev.setAmbienteRevList(List.of(ambienteRev));
        localRevDocRepository.save(localRev);

        LocalRevDoc foundRev = localRevDocRepository.findByLocalAndRevisaoId(local, 1L);
        assertThat(foundRev).isNotNull();
        assertThat(foundRev.getId()).isNotNull();
        assertThat(foundRev.getAmbienteRevList()).hasSize(1);
        assertThat(foundRev.getAmbienteRevList().get(0).getItemRevList().getFirst().getItem().getName())
                .isEqualTo("Item");
    }

    @Test
    void testLocalRevDuplicateThrows() {
        LocalRevDoc rev1 = new LocalRevDoc();
        rev1.setLocal(local);
        rev1.setRevisaoId(1L);
        rev1.setAmbienteRevList(List.of());
        localRevDocRepository.save(rev1);

        LocalRevDoc rev2 = new LocalRevDoc();
        rev2.setLocal(local);
        rev2.setRevisaoId(1L);
        rev2.setAmbienteRevList(List.of());

        assertThrows(DuplicateKeyException.class, () -> localRevDocRepository.save(rev2));
    }

    @Test
    void testLocalRevUpdate() {
        LocalRevDoc localRev = new LocalRevDoc();
        localRev.setLocal(local);
        localRev.setRevisaoId(1L);
        localRev.setAmbienteRevList(List.of());
        localRevDocRepository.save(localRev);

        LocalRevDoc found = localRevDocRepository.findByLocalAndRevisaoId(local, 1L);
        found.setAmbienteRevList(List.of());
        localRevDocRepository.save(found);

        LocalRevDoc updated = localRevDocRepository.findByLocalAndRevisaoId(local, 1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getRevisaoId()).isEqualTo(1L);
    }

    @Test
    void testLocalRevDelete() {
        LocalRevDoc localRev = new LocalRevDoc();
        localRev.setLocal(local);
        localRev.setRevisaoId(1L);
        localRevDocRepository.save(localRev);

        localRevDocRepository.deleteByLocalAndRevisaoId(local, 1L);
        LocalRevDoc found = localRevDocRepository.findByLocalAndRevisaoId(local, 1L);
        assertThat(found).isNull();
    }
}