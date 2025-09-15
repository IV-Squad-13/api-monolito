package com.squad13.apimonolito.editor.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDoc;
import com.squad13.apimonolito.models.editor.mongo.ItemDoc;
import com.squad13.apimonolito.mongo.editor.AmbienteDocRepository;
import com.squad13.apimonolito.mongo.editor.ItemDocRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AmbienteDocTest {

    @Autowired
    private ItemDocRepository itemDocRepository;

    @Autowired
    private AmbienteDocRepository ambienteDocRepository;

    @BeforeEach
    void cleanDatabase() {
        itemDocRepository.deleteAll();
        ambienteDocRepository.deleteAll();
    }

    @Test
    void testAmbientePersistence() {
        ItemDoc item = new ItemDoc();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        AmbienteDoc ambiente = new AmbienteDoc();
        ambiente.setName("Ambiente");
        ambiente.setCatalogId(1L);
        ambiente.setInSync(true);
        ambiente.setItemDocList(List.of(item));
        ambienteDocRepository.save(ambiente);

        assertThat(ambiente).isNotNull();
        assertThat(ambiente.getId()).isNotNull();
        assertThat(ambiente.getName()).isEqualTo("Ambiente");

        AmbienteDoc foundAmbiente = ambienteDocRepository.findByName("Ambiente");
        assertThat(foundAmbiente).isNotNull();
        assertThat(foundAmbiente.getId()).isEqualTo(ambiente.getId());
        assertThat(foundAmbiente.getItemDocList().getFirst().getName()).isEqualTo("Item");
    }

    @Test
    void testAmbienteUpdate() {
        ItemDoc item = new ItemDoc();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        AmbienteDoc ambiente = new AmbienteDoc();
        ambiente.setName("Ambiente");
        ambiente.setCatalogId(1L);
        ambiente.setInSync(true);
        ambiente.setItemDocList(List.of(item));
        ambienteDocRepository.save(ambiente);

        AmbienteDoc foundAmbiente = ambienteDocRepository.findByName("Ambiente");
        foundAmbiente.setName("Ambiente2");
        ambienteDocRepository.save(foundAmbiente);

        AmbienteDoc updated = ambienteDocRepository.findByName("Ambiente2");
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Ambiente2");
    }

    @Test
    void testAmbienteDelete() {
        ambienteDocRepository.deleteAllByName("Ambiente");
        AmbienteDoc foundAmbiente = ambienteDocRepository.findByName("Ambiente");

        assertThat(foundAmbiente).isNull();
    }
}