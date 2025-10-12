package com.squad13.apimonolito.editor.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDoc;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.ItemDoc;
import com.squad13.apimonolito.mongo.editor.AmbienteDocRepository;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
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

    @Autowired
    private EspecificacaoDocRepository empDocRepository;

    private EspecificacaoDoc empDoc;

    @BeforeEach
    void cleanDatabase() {
        itemDocRepository.deleteAll();
        ambienteDocRepository.deleteAll();
        empDocRepository.deleteAll();

        empDoc = new EspecificacaoDoc();
        empDoc.setName("Doc Empreendimento B");
        empDoc.setEmpreendimentoId(1L);
        empDoc.setDesc("Descricao B");
        empDoc.setObs("Observacao B");
        empDocRepository.save(empDoc);
    }

    @Test
    void testAmbientePersistence() {
        ItemDoc item = new ItemDoc();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setEspecificacaoDoc(empDoc);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        AmbienteDoc ambiente = new AmbienteDoc();
        ambiente.setName("Ambiente");
        ambiente.setCatalogId(1L);
        ambiente.setEspecificacaoDoc(empDoc);
        ambiente.setInSync(true);
        ambiente.setItemDocList(List.of(item));
        ambienteDocRepository.save(ambiente);

        assertThat(ambiente).isNotNull();
        assertThat(ambiente.getId()).isNotNull();
        assertThat(ambiente.getName()).isEqualTo("Ambiente");
        assertThat(ambiente.getEspecificacaoDoc()).isEqualTo(empDoc);

        AmbienteDoc foundAmbiente = ambienteDocRepository.findByName("Ambiente");
        assertThat(foundAmbiente).isNotNull();
        assertThat(foundAmbiente.getId()).isEqualTo(ambiente.getId());
        assertThat(foundAmbiente.getItemDocList().getFirst().getName()).isEqualTo("Item");
        assertThat(foundAmbiente.getEspecificacaoDoc().getId()).isEqualTo(empDoc.getId());
    }

    @Test
    void testAmbienteUpdate() {
        ItemDoc item = new ItemDoc();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setEspecificacaoDoc(empDoc);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        AmbienteDoc ambiente = new AmbienteDoc();
        ambiente.setName("Ambiente");
        ambiente.setCatalogId(1L);
        ambiente.setEspecificacaoDoc(empDoc);
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