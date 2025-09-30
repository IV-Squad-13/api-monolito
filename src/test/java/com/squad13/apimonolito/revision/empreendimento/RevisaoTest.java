package com.squad13.apimonolito.revision.empreendimento;


import com.squad13.apimonolito.models.editor.mongo.EmpreendimentoDoc;
import com.squad13.apimonolito.models.editor.relational.Empreendimento;
import com.squad13.apimonolito.models.revision.mongo.EmpreendimentoRevDoc;
import com.squad13.apimonolito.models.revision.relational.Revisao;
import com.squad13.apimonolito.mongo.editor.EmpreendimentoDocRepository;
import com.squad13.apimonolito.mongo.revision.EmpreendimentoRevDocRepository;
import com.squad13.apimonolito.repository.editor.EmpreendimentoRepository;
import com.squad13.apimonolito.repository.revision.RevisaoRepository;
import com.squad13.apimonolito.util.enums.EmpreendimentoStatusEnum;
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
class RevisaoTest {

    @Autowired
    private EmpreendimentoRepository empreendimentoRepository;

    @Autowired
    private RevisaoRepository revisaoRepository;

    @Autowired
    private EmpreendimentoDocRepository empreendimentoDocRepository;

    @Autowired
    private EmpreendimentoRevDocRepository empreendimentoRevDocRepository;

    @BeforeEach
    void cleanDatabase() {
        empreendimentoRevDocRepository.deleteAll();
        empreendimentoDocRepository.deleteAll();
        revisaoRepository.deleteAll();
        empreendimentoRepository.deleteAll();
    }

    @Test
    void testJpaPersistenceWithRevisao() {
        Empreendimento empreendimento = new Empreendimento();
        empreendimento.setName("Empreendimento A");
        empreendimento.setStatusEnum(EmpreendimentoStatusEnum.INICIADO);

        Empreendimento savedEmp = empreendimentoRepository.save(empreendimento);
        assertThat(savedEmp.getId()).isNotNull();

        Revisao revisao = new Revisao();
        revisao.setStatusEnum(RevisaoStatusEnum.INICIADA);
        revisao.setEmpreendimento(savedEmp);

        Revisao savedRevisao = revisaoRepository.save(revisao);

        assertThat(savedRevisao.getId()).isNotNull();
        assertThat(savedRevisao.getEmpreendimento().getId()).isEqualTo(savedEmp.getId());

        Revisao foundRev = revisaoRepository.findById(savedRevisao.getId()).orElseThrow();
        assertThat(foundRev.getStatusEnum()).isEqualTo(RevisaoStatusEnum.INICIADA);
        assertThat(foundRev.getEmpreendimento().getName()).isEqualTo("Empreendimento A");
    }

    @Test
    void testMongoPersistenceReferencingJpaEntity() {
        Empreendimento jpaEntity = new Empreendimento();
        jpaEntity.setName("Empreendimento B");
        jpaEntity.setStatusEnum(EmpreendimentoStatusEnum.FINALIZADO);

        Empreendimento savedEntity = empreendimentoRepository.save(jpaEntity);
        assertThat(savedEntity.getId()).isNotNull();

        EmpreendimentoDoc doc = new EmpreendimentoDoc();
        doc.setName("Doc Empreendimento B");
        doc.setEmpreendimentoId(savedEntity.getId());
        doc.setDesc("Descricao B");
        doc.setObs("Observacao B");

        EmpreendimentoDoc savedDoc = empreendimentoDocRepository.save(doc);

        assertThat(savedDoc.getId()).isNotNull();

        EmpreendimentoDoc foundDoc = empreendimentoDocRepository.findById(savedDoc.getId()).orElseThrow();

        assertThat(foundDoc.getName()).isEqualTo("Doc Empreendimento B");
        assertThat(foundDoc.getEmpreendimentoId()).isEqualTo(savedEntity.getId());
        assertThat(foundDoc.getCreated()).isNotNull();
        assertThat(foundDoc.getUpdated()).isNotNull();
    }

    @Test
    void testConsistencyBetweenJpaAndMongo() {
        Empreendimento e = new Empreendimento();
        e.setName("Empreendimento C");
        e.setStatusEnum(EmpreendimentoStatusEnum.INICIADO);
        Empreendimento savedEntity = empreendimentoRepository.save(e);

        EmpreendimentoDoc doc = new EmpreendimentoDoc();
        doc.setName("Doc Empreendimento C");
        doc.setEmpreendimentoId(savedEntity.getId());
        EmpreendimentoDoc savedDoc = empreendimentoDocRepository.save(doc);

        assertThat(savedDoc.getEmpreendimentoId()).isEqualTo(savedEntity.getId());

        Optional<Empreendimento> foundEntityOpt = empreendimentoRepository.findById(savedDoc.getEmpreendimentoId());

        assertThat(foundEntityOpt).isPresent();
        assertThat(foundEntityOpt.get().getName()).isEqualTo("Empreendimento C");
    }

    @Test
    void testCreateEmpreendimentoRevisionDoc() {
        Empreendimento emp = new Empreendimento();
        emp.setName("Empreendimento D");
        emp.setStatusEnum(EmpreendimentoStatusEnum.INICIADO);
        Empreendimento savedEmp = empreendimentoRepository.save(emp);

        Revisao revisao = new Revisao();
        revisao.setEmpreendimento(savedEmp);
        revisao.setStatusEnum(RevisaoStatusEnum.INICIADA);
        Revisao savedRevisao = revisaoRepository.save(revisao);

        EmpreendimentoDoc doc = new EmpreendimentoDoc();
        doc.setName("Doc Empreendimento D");
        doc.setEmpreendimentoId(savedEmp.getId());
        EmpreendimentoDoc savedDoc = empreendimentoDocRepository.save(doc);

        EmpreendimentoRevDoc revDoc = new EmpreendimentoRevDoc();
        revDoc.setRevisaoId(savedRevisao.getId());
        revDoc.setEmpreendimento(savedDoc);
        revDoc.setNameApproved(true);
        revDoc.setDescApproved(false);
        revDoc.setObsApproved(false);

        EmpreendimentoRevDoc savedRevDoc = empreendimentoRevDocRepository.save(revDoc);

        assertThat(savedRevDoc.getId()).isNotNull();
        assertThat(savedRevDoc.getRevisaoId()).isEqualTo(savedRevisao.getId());
        assertThat(savedRevDoc.getEmpreendimento().getId()).isEqualTo(savedDoc.getId());
        assertThat(savedRevDoc.isNameApproved()).isTrue();
    }
}