package com.squad13.apimonolito.editor.empreendimento;

import com.squad13.apimonolito.models.editor.mongo.EmpreendimentoDoc;
import com.squad13.apimonolito.mongo.editor.EmpreendimentoDocRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataMongoTest
class EmpreendimentoMongoRepositoryTest {

    @Autowired
    private EmpreendimentoDocRepository empreendimentoDocRepository;

    @Test
    void testSaveAndFindEmpreendimento() {
        EmpreendimentoDoc eDoc = new EmpreendimentoDoc();
        eDoc.setName("Empreendimento A");
        eDoc.setEmpreendimentoId(1L);

        EmpreendimentoDoc saved = empreendimentoDocRepository.save(eDoc);

        Optional<EmpreendimentoDoc> found = empreendimentoDocRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Empreendimento A");
        assertThat(found.get().getEmpreendimentoId()).isEqualTo(1L);
    }
}