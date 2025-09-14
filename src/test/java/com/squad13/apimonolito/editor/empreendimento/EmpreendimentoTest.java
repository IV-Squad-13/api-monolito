package com.squad13.apimonolito.editor.empreendimento;

import com.squad13.apimonolito.models.editor.mongo.EmpreendimentoDoc;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.mongo.editor.EmpreendimentoDocRepository;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.util.MongoRepositoryNavigator;
import com.squad13.apimonolito.util.enums.EmpreendimentoStatusEnum;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EmpreendimentoTest {

    @Autowired
    private EmpreendimentoRepository empreendimentoRepository;

    @Autowired
    private EmpreendimentoDocRepository empreendimentoDocRepository;

    @Autowired
    private MongoRepositoryNavigator mongoRepositoryNavigator;

    @Test
    void testFullPersistence() {
        Empreendimento e = new Empreendimento();
        e.setName("Empreendimento Z");
        e.setStatusEnum(EmpreendimentoStatusEnum.EM_ANDAMENTO);

        Empreendimento saved = empreendimentoRepository.save(e);
        assertThat(saved.getId()).isNotNull();

        Empreendimento found = empreendimentoRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Empreendimento Z");

        EmpreendimentoDoc eDoc = new EmpreendimentoDoc();
        eDoc.setName("Doc do Empreendimento Z");
        eDoc.setEmpreendimentoId(saved.getId());
        eDoc.setDesc("Lorem Ipsum");
        eDoc.setObs("Dolor Sit Amet");

        EmpreendimentoDoc savedDoc = empreendimentoDocRepository.save(eDoc);
        assertThat(savedDoc.getId()).isNotNull();

        Optional<EmpreendimentoDoc> foundDoc = empreendimentoDocRepository.findById(savedDoc.getId());
        assertThat(foundDoc).isPresent();
        assertThat(foundDoc.get().getEmpreendimentoId()).isEqualTo(saved.getId());
        assertThat(foundDoc.get().getName()).isEqualTo("Doc do Empreendimento Z");
    }
}
