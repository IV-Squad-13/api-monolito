package com.squad13.apimonolito.revision.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.models.revision.mongo.AmbienteRevDocElement;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDocElement;
import com.squad13.apimonolito.models.revision.mongo.LocalRevDocElement;
import com.squad13.apimonolito.mongo.editor.AmbienteDocElementRepository;
import com.squad13.apimonolito.mongo.editor.ItemDocElementRepository;
import com.squad13.apimonolito.mongo.editor.LocalDocRepository;
import com.squad13.apimonolito.mongo.revision.AmbienteRevDocElementRepository;
import com.squad13.apimonolito.mongo.revision.ItemRevDocElementRepository;
import com.squad13.apimonolito.mongo.revision.LocalRevDocElementRepository;
import com.squad13.apimonolito.util.enums.LocalEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class LocalRevDocTest {

    @Autowired
    private ItemDocElementRepository itemDocRepository;

    @Autowired
    private ItemRevDocElementRepository itemRevDocRepository;

    @Autowired
    private AmbienteDocElementRepository ambienteDocRepository;

    @Autowired
    private AmbienteRevDocElementRepository ambienteRevDocRepository;

    @Autowired
    private LocalDocRepository localDocRepository;

    @Autowired
    private LocalRevDocElementRepository localRevDocRepository;

    private ItemDocElement item;
    private AmbienteDocElement ambiente;
    private LocalDoc local;

    @BeforeEach
    void cleanDatabase() {
        localRevDocRepository.deleteAll();
        ambienteRevDocRepository.deleteAll();
        itemRevDocRepository.deleteAll();
        localDocRepository.deleteAll();
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
        ambiente.setItemDocList(List.of(item));
        ambienteDocRepository.save(ambiente);

        local = new LocalDoc();
        local.setLocal(LocalEnum.AREA_COMUM);
        local.setAmbienteDocList(List.of(ambiente));
        localDocRepository.save(local);
    }

    @Test
    void testLocalRevPersistence() {
        ItemRevDocElement itemRev = new ItemRevDocElement();
        itemRev.setItem(item);
        itemRev.setRevisaoId(1L);
        itemRev.setApproved(true);
        itemRev.setComment("First revision");
        itemRevDocRepository.save(itemRev);

        AmbienteRevDocElement ambienteRev = new AmbienteRevDocElement();
        ambienteRev.setAmbiente(ambiente);
        ambienteRev.setRevisaoId(1L);
        ambienteRev.setItemRevList(List.of(itemRev));
        ambienteRevDocRepository.save(ambienteRev);

        LocalRevDocElement localRev = new LocalRevDocElement();
        localRev.setLocal(local);
        localRev.setRevisaoId(1L);
        localRev.setAmbienteRevList(List.of(ambienteRev));
        localRevDocRepository.save(localRev);

        LocalRevDocElement foundRev = localRevDocRepository.findByLocalAndRevisaoId(local, 1L);
        assertThat(foundRev).isNotNull();
        assertThat(foundRev.getId()).isNotNull();
        assertThat(foundRev.getAmbienteRevList()).hasSize(1);
        assertThat(foundRev.getAmbienteRevList().getFirst().getItemRevList().getFirst().getItem().getName())
                .isEqualTo("Item");
    }

    @Test
    void testLocalRevDuplicateThrows() {
        LocalRevDocElement rev1 = new LocalRevDocElement();
        rev1.setLocal(local);
        rev1.setRevisaoId(1L);
        rev1.setAmbienteRevList(List.of());
        localRevDocRepository.save(rev1);

        LocalRevDocElement rev2 = new LocalRevDocElement();
        rev2.setLocal(local);
        rev2.setRevisaoId(1L);
        rev2.setAmbienteRevList(List.of());

        assertThrows(DataIntegrityViolationException.class,
                () -> localRevDocRepository.save(rev2));
    }

    @Test
    void testLocalRevUpdate() {
        LocalRevDocElement localRev = new LocalRevDocElement();
        localRev.setLocal(local);
        localRev.setRevisaoId(1L);
        localRev.setAmbienteRevList(List.of());
        localRevDocRepository.save(localRev);

        LocalRevDocElement found = localRevDocRepository.findByLocalAndRevisaoId(local, 1L);
        found.setAmbienteRevList(List.of());
        localRevDocRepository.save(found);

        LocalRevDocElement updated = localRevDocRepository.findByLocalAndRevisaoId(local, 1L);
        assertThat(updated).isNotNull();
        assertThat(updated.getRevisaoId()).isEqualTo(1L);
    }

    @Test
    void testLocalRevDelete() {
        LocalRevDocElement localRev = new LocalRevDocElement();
        localRev.setLocal(local);
        localRev.setRevisaoId(1L);
        localRevDocRepository.save(localRev);

        localRevDocRepository.deleteByLocalAndRevisaoId(local, 1L);
        LocalRevDocElement found = localRevDocRepository.findByLocalAndRevisaoId(local, 1L);
        assertThat(found).isNull();
    }
}