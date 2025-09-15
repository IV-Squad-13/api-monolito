package com.squad13.apimonolito.revision.especificacoes;

import com.mongodb.DuplicateKeyException;
import com.squad13.apimonolito.models.editor.mongo.ItemDoc;
import com.squad13.apimonolito.models.revision.mongo.ItemRevDoc;
import com.squad13.apimonolito.mongo.editor.ItemDocRepository;
import com.squad13.apimonolito.mongo.revision.ItemRevDocRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ItemRevDocTest {

    @Autowired
    private ItemDocRepository itemDocRepository;

    @Autowired
    private ItemRevDocRepository itemRevDocRepository;

    private ItemDoc item;

    @BeforeEach
    void setup() {
        itemRevDocRepository.deleteAll();
        itemDocRepository.deleteAll();

        item = new ItemDoc();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        assertThat(itemDocRepository.findAll()).hasSize(1);
    }

    @Test
    void testItemRevPersistence() {
        ItemRevDoc itemRev = new ItemRevDoc();
        itemRev.setItem(item);
        itemRev.setRevisaoId(1L);
        itemRev.setApproved(true);
        itemRev.setComment("Revisão do item");

        itemRevDocRepository.save(itemRev);

        ItemRevDoc foundItemRev = itemRevDocRepository.findByItemAndRevisaoId(item, 1L);
        assertThat(foundItemRev).isNotNull();
        assertThat(foundItemRev.getId()).isEqualTo(itemRev.getId());
        assertThat(foundItemRev.getComment()).isEqualTo(itemRev.getComment());
        assertThat(foundItemRev.getItem().getName()).isEqualTo(item.getName());
        assertThat(foundItemRev.isApproved()).isTrue();
    }

    @Test
    void testItemRevUpdate() {
        ItemRevDoc itemRev = new ItemRevDoc();
        itemRev.setItem(item);
        itemRev.setRevisaoId(1L);
        itemRev.setApproved(false);
        itemRev.setComment("Revisão do item");
        itemRevDocRepository.save(itemRev);

        ItemRevDoc found = itemRevDocRepository.findByItemAndRevisaoId(item, 1L);
        found.setApproved(true);
        found.setComment("Revisão Aprovada");
        itemRevDocRepository.save(found);

        ItemRevDoc updated = itemRevDocRepository.findByItemAndRevisaoId(item, 1L);
        assertThat(updated.isApproved()).isTrue();
        assertThat(updated.getComment()).isEqualTo("Revisão Aprovada");
    }

    @Test
    void testItemRevDelete() {
        ItemRevDoc itemRev = new ItemRevDoc();
        itemRev.setItem(item);
        itemRev.setRevisaoId(1L);
        itemRev.setApproved(false);
        itemRev.setComment("O item deve ser removido");
        itemRevDocRepository.save(itemRev);

        itemRevDocRepository.delete(itemRev);
        ItemRevDoc found = itemRevDocRepository.findByItemAndRevisaoId(item, 1L);
        assertThat(found).isNull();
    }

    @Test
    void testDuplicateItemRevThrows() {
        ItemRevDoc rev1 = new ItemRevDoc();
        rev1.setItem(item);
        rev1.setRevisaoId(1L);
        rev1.setApproved(true);
        rev1.setComment("First revision");
        itemRevDocRepository.save(rev1);

        ItemRevDoc rev2 = new ItemRevDoc();
        rev2.setItem(item);
        rev2.setRevisaoId(1L);
        rev2.setApproved(false);
        rev2.setComment("Duplicate revision");

        assertThrows(DuplicateKeyException.class, () -> itemRevDocRepository.save(rev2));
    }
}