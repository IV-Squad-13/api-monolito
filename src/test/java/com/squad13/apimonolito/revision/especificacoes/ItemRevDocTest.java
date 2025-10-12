package com.squad13.apimonolito.revision.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDocElement;
import com.squad13.apimonolito.mongo.editor.ItemDocElementRepository;
import com.squad13.apimonolito.mongo.revision.ItemRevDocElementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ItemRevDocTest {

    @Autowired
    private ItemDocElementRepository itemDocRepository;

    @Autowired
    private ItemRevDocElementRepository itemRevDocRepository;

    private ItemDocElement item;

    @BeforeEach
    void setup() {
        itemRevDocRepository.deleteAll();
        itemDocRepository.deleteAll();

        item = new ItemDocElement();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        assertThat(itemDocRepository.findAll()).hasSize(1);
    }

    @Test
    void testItemRevPersistence() {
        ItemRevDocElement itemRev = new ItemRevDocElement();
        itemRev.setItem(item);
        itemRev.setRevisaoId(1L);
        itemRev.setApproved(true);
        itemRev.setComment("Revisão do item");

        itemRevDocRepository.save(itemRev);

        ItemRevDocElement foundItemRev = itemRevDocRepository.findByItemAndRevisaoId(item, 1L);
        assertThat(foundItemRev).isNotNull();
        assertThat(foundItemRev.getId()).isEqualTo(itemRev.getId());
        assertThat(foundItemRev.getComment()).isEqualTo(itemRev.getComment());
        assertThat(foundItemRev.getItem().getName()).isEqualTo(item.getName());
        assertThat(foundItemRev.isApproved()).isTrue();
    }

    @Test
    void testItemRevUpdate() {
        ItemRevDocElement itemRev = new ItemRevDocElement();
        itemRev.setItem(item);
        itemRev.setRevisaoId(1L);
        itemRev.setApproved(false);
        itemRev.setComment("Revisão do item");
        itemRevDocRepository.save(itemRev);

        ItemRevDocElement found = itemRevDocRepository.findByItemAndRevisaoId(item, 1L);
        found.setApproved(true);
        found.setComment("Revisão Aprovada");
        itemRevDocRepository.save(found);

        ItemRevDocElement updated = itemRevDocRepository.findByItemAndRevisaoId(item, 1L);
        assertThat(updated.isApproved()).isTrue();
        assertThat(updated.getComment()).isEqualTo("Revisão Aprovada");
    }

    @Test
    void testItemRevDelete() {
        ItemRevDocElement itemRev = new ItemRevDocElement();
        itemRev.setItem(item);
        itemRev.setRevisaoId(1L);
        itemRev.setApproved(false);
        itemRev.setComment("O item deve ser removido");
        itemRevDocRepository.save(itemRev);

        itemRevDocRepository.delete(itemRev);
        ItemRevDocElement found = itemRevDocRepository.findByItemAndRevisaoId(item, 1L);
        assertThat(found).isNull();
    }

    @Test
    void testDuplicateItemRevThrows() {
        ItemRevDocElement rev1 = new ItemRevDocElement();
        rev1.setItem(item);
        rev1.setRevisaoId(1L);
        rev1.setApproved(true);
        rev1.setComment("First revision");
        itemRevDocRepository.save(rev1);

        ItemRevDocElement rev2 = new ItemRevDocElement();
        rev2.setItem(item);
        rev2.setRevisaoId(1L);
        rev2.setApproved(false);
        rev2.setComment("Duplicate revision");

        assertThrows(DataIntegrityViolationException.class,
                () -> itemRevDocRepository.save(rev2));
    }
}