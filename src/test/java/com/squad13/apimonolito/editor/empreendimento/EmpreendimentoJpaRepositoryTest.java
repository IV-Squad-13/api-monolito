package com.squad13.apimonolito.editor.empreendimento;

import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.util.enums.EmpreendimentoStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmpreendimentoJpaRepositoryTest {

    @Autowired
    private EmpreendimentoRepository empreendimentoRepository;

    @Test
    void testSaveAndFindEmpreendimento() {
        Empreendimento e = new Empreendimento();
        e.setName("Empreendimento A");
        e.setStatusEnum(EmpreendimentoStatusEnum.EM_ANDAMENTO);

        Empreendimento saved = empreendimentoRepository.save(e);

        Optional<Empreendimento> found = empreendimentoRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Empreendimento A");
    }
}