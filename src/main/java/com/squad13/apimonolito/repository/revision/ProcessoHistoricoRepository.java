package com.squad13.apimonolito.repository.revision;

import com.squad13.apimonolito.models.revision.relational.ProcessoHistorico;
import com.squad13.apimonolito.util.enums.ProcActionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessoHistoricoRepository extends JpaRepository<ProcessoHistorico, Long> {
    List<ProcessoHistorico> findByEmp_Id(Long empId);

    void deleteAllByEmp_Id(Long empId);
}
