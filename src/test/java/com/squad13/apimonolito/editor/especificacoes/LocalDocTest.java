package com.squad13.apimonolito.editor.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDocElement;
import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.mongo.ItemDocElement;
import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.mongo.editor.AmbienteDocElementRepository;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.mongo.editor.ItemDocElementRepository;
import com.squad13.apimonolito.mongo.editor.LocalDocRepository;
import com.squad13.apimonolito.util.enums.LocalEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DataMongoTest
class LocalDocTest {

    @Autowired
    private ItemDocElementRepository itemDocRepository;

    @Autowired
    private AmbienteDocElementRepository ambienteDocRepository;

    @Autowired
    private LocalDocRepository localDocRepository;

    @Autowired
    private EspecificacaoDocRepository empDocRepository;

    private EspecificacaoDoc empDoc;

    @BeforeEach
    void cleanDatabase() {
        itemDocRepository.deleteAll();
        ambienteDocRepository.deleteAll();
        localDocRepository.deleteAll();
        empDocRepository.deleteAll();

        empDoc = new EspecificacaoDoc();
        empDoc.setName("Doc Empreendimento B");
        empDoc.setEmpreendimentoId(1L);
        empDoc.setDesc("Descricao B");
        empDoc.setObs("Observacao B");
        empDocRepository.save(empDoc);
    }

    @Test
    void testLocalPersistence() {
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

        LocalDoc local = new LocalDoc();
        local.setEspecificacaoDoc(empDoc);
        local.setLocal(LocalEnum.UNIDADES_PRIVATIVAS);
        local.setAmbienteDocList(List.of(ambiente));
        localDocRepository.save(local);

        List<LocalDoc> foundLocals = localDocRepository.findByLocal(LocalEnum.UNIDADES_PRIVATIVAS);
        LocalDoc foundLocal = localDocRepository.findById(local.getId())
                .orElseThrow(() -> new RuntimeException("Local não encontrado"));

        assertThat(foundLocals).isNotEmpty();
        assertThat(foundLocals).contains(foundLocal);
        assertThat(foundLocal.getId()).isEqualTo(local.getId());
        assertThat(foundLocal.getLocal()).isNull();
        assertThat(foundLocal.getAmbienteDocList()).hasSize(1);
        assertThat(foundLocal.getAmbienteDocList().getFirst().getName()).isEqualTo("Ambiente");
    }

    @Test
    void testLocalUpdate() {
        LocalDoc local = new LocalDoc();
        local.setEspecificacaoDoc(empDoc);
        local.setLocal(LocalEnum.UNIDADES_PRIVATIVAS);
        localDocRepository.save(local);

        LocalDoc foundLocal = localDocRepository.findById(local.getId())
                .orElseThrow(() -> new RuntimeException("Local não encontrado"));
        foundLocal.setLocal(LocalEnum.AREA_COMUM);
        localDocRepository.save(foundLocal);

        LocalDoc updated = localDocRepository.findById(local.getId())
                .orElseThrow(() -> new RuntimeException("Local não encontrado após update"));

        assertThat(updated).isNotNull();
        assertThat(updated.getLocal()).isEqualTo(LocalEnum.AREA_COMUM);
    }

    @Test
    void testLocalDelete() {
        LocalDoc local = new LocalDoc();
        local.setEspecificacaoDoc(empDoc);
        local.setLocal(LocalEnum.UNIDADES_PRIVATIVAS);
        localDocRepository.save(local);

        localDocRepository.delete(local);

        boolean exists = localDocRepository.findById(local.getId()).isPresent();
        assertThat(exists).isFalse();
    }
}