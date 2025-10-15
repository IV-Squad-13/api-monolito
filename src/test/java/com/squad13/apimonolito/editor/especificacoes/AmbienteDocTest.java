package com.squad13.apimonolito.editor.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.mongo.editor.AmbienteDocElementRepository;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.editor.ItemDocElementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AmbienteDocTest {

    @Autowired
    private ItemDocElementRepository itemDocRepository;

    @Autowired
    private AmbienteDocElementRepository ambienteDocRepository;

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
        ItemDocElement item = new ItemDocElement();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setEspecificacaoDoc(empDoc);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        AmbienteDocElement ambiente = new AmbienteDocElement();
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

        AmbienteDocElement foundAmbiente = ambienteDocRepository.findByName("Ambiente").orElse(null);
        assertThat(foundAmbiente).isNotNull();
        assertThat(foundAmbiente.getId()).isEqualTo(ambiente.getId());
        assertThat(foundAmbiente.getItemDocList().getFirst().getName()).isEqualTo("Item");
        assertThat(foundAmbiente.getEspecificacaoDoc().getId()).isEqualTo(empDoc.getId());
    }

    @Test
    void testAmbienteUpdate() {
        ItemDocElement item = new ItemDocElement();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setEspecificacaoDoc(empDoc);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        AmbienteDocElement ambiente = new AmbienteDocElement();
        ambiente.setName("Ambiente");
        ambiente.setCatalogId(1L);
        ambiente.setEspecificacaoDoc(empDoc);
        ambiente.setInSync(true);
        ambiente.setItemDocList(List.of(item));
        ambienteDocRepository.save(ambiente);

        AmbienteDocElement foundAmbiente = ambienteDocRepository.findByName("Ambiente").orElse(null);
        foundAmbiente.setName("Ambiente2");
        ambienteDocRepository.save(foundAmbiente);

        AmbienteDocElement updated = ambienteDocRepository.findByName("Ambiente2").orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Ambiente2");
    }

    @Test
    void testAmbienteDelete() {
        ambienteDocRepository.deleteAllByName("Ambiente");
        AmbienteDocElement foundAmbiente = ambienteDocRepository.findByName("Ambiente").orElse(null);

        assertThat(foundAmbiente).isNull();
    }
}