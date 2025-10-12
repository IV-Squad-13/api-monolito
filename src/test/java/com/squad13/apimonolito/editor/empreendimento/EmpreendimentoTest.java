package com.squad13.apimonolito.editor.empreendimento;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.repository.revision.RevisaoRepository;
import com.squad13.apimonolito.util.enums.EmpStatusEnum;
import com.squad13.apimonolito.util.enums.RevisaoStatusEnum;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
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
    private RevisaoRepository revisaoRepository;

    @Autowired
    private EspecificacaoDocRepository especificacaoDocRepository;

    @BeforeEach
    void cleanDatabase() {
        revisaoRepository.deleteAll();
        empreendimentoRepository.deleteAll();
        especificacaoDocRepository.deleteAll();
    }

    @Test
    void testJpaPersistenceWithRevisao() {
        Empreendimento empreendimento = new Empreendimento();
        empreendimento.setName("Empreendimento A");
        empreendimento.setStatus(EmpStatusEnum.INICIADO);

        Empreendimento savedEmp = empreendimentoRepository.save(empreendimento);
        assertThat(savedEmp.getId()).isNotNull();

        Revisao revisao = new Revisao();
        revisao.setStatusEnum(RevisaoStatusEnum.INICIADA);
        revisao.setEmpreendimento(savedEmp);

        Revisao savedRevisao = revisaoRepository.save(revisao);
        assertThat(savedRevisao.getId()).isNotNull();
        assertThat(savedRevisao.getEmpreendimento().getId()).isEqualTo(savedEmp.getId());

        Revisao foundRev = revisaoRepository.findById(savedRevisao.getId()).orElseThrow(
                () -> new RuntimeException("Revisão não encontrada"));
        assertThat(foundRev.getId()).isEqualTo(savedRevisao.getId());
        assertThat(foundRev.getEmpreendimento().getName()).isEqualTo("Empreendimento A");
        assertThat(foundRev.getStatusEnum()).isEqualTo(RevisaoStatusEnum.INICIADA);
    }

    @Test
    void testMongoPersistenceReferencingJpaEntity() {
        Empreendimento jpaEntity = new Empreendimento();
        jpaEntity.setName("Empreendimento B");
        jpaEntity.setStatus(EmpStatusEnum.FINALIZADO);

        Empreendimento savedEntity = empreendimentoRepository.save(jpaEntity);
        assertThat(savedEntity.getId()).isNotNull();

        EspecificacaoDoc doc = new EspecificacaoDoc();
        doc.setName("Doc Empreendimento B");
        doc.setEmpreendimentoId(savedEntity.getId());
        doc.setDesc("Descricao B");
        doc.setObs("Observacao B");

        EspecificacaoDoc savedDoc = especificacaoDocRepository.save(doc);
        assertThat(savedDoc.getId()).isNotNull();

        EspecificacaoDoc foundDoc = especificacaoDocRepository.findById(savedDoc.getId()).orElseThrow();

        assertThat(foundDoc.getName()).isEqualTo("Doc Empreendimento B");
        assertThat(foundDoc.getEmpreendimentoId()).isEqualTo(savedEntity.getId());
        assertThat(foundDoc.getCreated()).isNotNull();
        assertThat(foundDoc.getUpdated()).isNotNull();
    }

    @Test
    void testConsistencyBetweenJpaAndMongo() {
        Empreendimento e = new Empreendimento();
        e.setName("Empreendimento C");
        e.setStatus(EmpStatusEnum.INICIADO);

        Empreendimento savedEntity = empreendimentoRepository.save(e);

        EspecificacaoDoc doc = new EspecificacaoDoc();
        doc.setName("Doc Empreendimento C");
        doc.setEmpreendimentoId(savedEntity.getId());

        EspecificacaoDoc savedDoc = especificacaoDocRepository.save(doc);

        assertThat(savedDoc.getEmpreendimentoId()).isEqualTo(savedEntity.getId());

        Optional<Empreendimento> foundEntityOpt =
                empreendimentoRepository.findById(savedDoc.getEmpreendimentoId());
        assertThat(foundEntityOpt).isPresent();
        assertThat(foundEntityOpt.get().getName()).isEqualTo("Empreendimento C");
    }
}