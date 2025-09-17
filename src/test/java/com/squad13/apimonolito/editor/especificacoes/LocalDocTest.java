package com.squad13.apimonolito.editor.especificacoes;

import com.squad13.apimonolito.models.editor.mongo.AmbienteDoc;
import com.squad13.apimonolito.models.editor.mongo.EmpreendimentoDoc;
import com.squad13.apimonolito.models.editor.mongo.ItemDoc;
import com.squad13.apimonolito.models.editor.mongo.LocalDoc;
import com.squad13.apimonolito.mongo.editor.AmbienteDocRepository;
import com.squad13.apimonolito.mongo.editor.EmpreendimentoDocRepository;
import com.squad13.apimonolito.mongo.editor.ItemDocRepository;
import com.squad13.apimonolito.mongo.editor.LocalDocRepository;
import com.squad13.apimonolito.util.enums.LocalEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LocalDocTest {

    @Autowired
    private ItemDocRepository itemDocRepository;

    @Autowired
    private AmbienteDocRepository ambienteDocRepository;

    @Autowired
    private LocalDocRepository localDocRepository;

    @Autowired
    private EmpreendimentoDocRepository empDocRepository;

    private EmpreendimentoDoc empDoc;

    @BeforeEach
    void cleanDatabase() {
        itemDocRepository.deleteAll();
        ambienteDocRepository.deleteAll();
        localDocRepository.deleteAll();
        empDocRepository.deleteAll();

        empDoc = new EmpreendimentoDoc();
        empDoc.setName("Doc Empreendimento B");
        empDoc.setEmpreendimentoId(1L);
        empDoc.setDesc("Descricao B");
        empDoc.setObs("Observacao B");
        empDocRepository.save(empDoc);
    }

    @Test
    void testLocalPersistence() {
        ItemDoc item = new ItemDoc();
        item.setName("Item");
        item.setCatalogId(1L);
        item.setEmpreendimentoDoc(empDoc);
        item.setInSync(true);
        item.setDesc("Desc");
        itemDocRepository.save(item);

        AmbienteDoc ambiente = new AmbienteDoc();
        ambiente.setName("Ambiente");
        ambiente.setCatalogId(1L);
        ambiente.setEmpreendimentoDoc(empDoc);
        ambiente.setInSync(true);
        ambiente.setItemDocList(List.of(item));
        ambienteDocRepository.save(ambiente);

        LocalDoc local = new LocalDoc();
        local.setName("Local");
        local.setCatalogId(1L);
        local.setEmpreendimentoDoc(empDoc);
        local.setLocal(LocalEnum.UNIDADE_PRIVATIVA);
        local.setInSync(true);
        local.setAmbienteDocList(List.of(ambiente));
        localDocRepository.save(local);

        List<LocalDoc> foundLocals = localDocRepository.findByLocal(LocalEnum.UNIDADE_PRIVATIVA);
        LocalDoc foundLocal = localDocRepository.findById(local.getId())
                .orElseThrow(() -> new RuntimeException("Local não encontrado"));

        assertThat(foundLocals).isNotEmpty();
        assertThat(foundLocals).contains(foundLocal);
        assertThat(foundLocal.getId()).isEqualTo(local.getId());
        assertThat(foundLocal.getName()).isNull();
        assertThat(foundLocal.getCatalogId()).isNull();
        assertThat(foundLocal.getAmbienteDocList()).hasSize(1);
        assertThat(foundLocal.getAmbienteDocList().getFirst().getName()).isEqualTo("Ambiente");
    }

    @Test
    void testLocalUpdate() {
        LocalDoc local = new LocalDoc();
        local.setName("Local");
        local.setCatalogId(1L);
        local.setEmpreendimentoDoc(empDoc);
        local.setLocal(LocalEnum.UNIDADE_PRIVATIVA);
        local.setInSync(true);
        localDocRepository.save(local);

        LocalDoc foundLocal = localDocRepository.findById(local.getId())
                .orElseThrow(() -> new RuntimeException("Local não encontrado"));
        foundLocal.setLocal(LocalEnum.AREA_COMUM);
        localDocRepository.save(foundLocal);

        LocalDoc updated = localDocRepository.findById(local.getId())
                .orElseThrow(() -> new RuntimeException("Local não encontrado após update"));

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isNull();
        assertThat(updated.getLocal()).isEqualTo(LocalEnum.AREA_COMUM);
    }

    @Test
    void testLocalDelete() {
        LocalDoc local = new LocalDoc();
        local.setName("Local");
        local.setCatalogId(1L);
        local.setEmpreendimentoDoc(empDoc);
        local.setLocal(LocalEnum.UNIDADE_PRIVATIVA);
        local.setInSync(true);
        localDocRepository.save(local);

        localDocRepository.delete(local);

        boolean exists = localDocRepository.findById(local.getId()).isPresent();
        assertThat(exists).isFalse();
    }
}