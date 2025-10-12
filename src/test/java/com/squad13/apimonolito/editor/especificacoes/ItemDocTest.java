package com.squad13.apimonolito.editor.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.ItemDoc;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.editor.ItemDocRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ItemDocTest {

    @Autowired
    private ItemDocRepository itemDocRepository;

    @Autowired
    private EspecificacaoDocRepository empDocRepository;

    private EspecificacaoDoc empDoc;

    @BeforeEach
    void cleanDatabase() {
        itemDocRepository.deleteAll();
        empDocRepository.deleteAll();

        empDoc = new EspecificacaoDoc();
        empDoc.setName("Doc Empreendimento B");
        empDoc.setEmpreendimentoId(1L);
        empDoc.setDesc("Descricao B");
        empDoc.setObs("Observacao B");
        empDocRepository.save(empDoc);
    }

    @Test
    void testItemPersistence() {
        ItemDoc item = new ItemDoc();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setEspecificacaoDoc(empDoc);
        item.setInSync(true);
        item.setDesc("Desc");

        itemDocRepository.save(item);

        assertThat(item).isNotNull();
        assertThat(item.getId()).isNotNull();
        assertThat(item.getName()).isEqualTo("Item");

        ItemDoc foundItem = itemDocRepository.findByName("Item");
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getId()).isEqualTo(item.getId());
    }

    @Test
    void testItemUpdate() {
        ItemDoc item = new ItemDoc();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setEspecificacaoDoc(empDoc);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        item.setName("Item1");
        itemDocRepository.save(item);

        ItemDoc foundItem = itemDocRepository.findByName("Item1");

        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getUpdated()).isNotNull();
        assertThat(foundItem.getName()).isEqualTo("Item1");
    }

    @Test
    void testItemDelete() {
        itemDocRepository.deleteAllByName("Item");
        ItemDoc foundItem = itemDocRepository.findByName("Item");

        assertThat(foundItem).isNull();
    }
}