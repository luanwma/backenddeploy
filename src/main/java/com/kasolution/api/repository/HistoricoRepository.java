package com.kasolution.api.repository;

import com.kasolution.api.model.HistoricoTarefa;
import com.kasolution.api.model.Tarefa;
import com.kasolution.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HistoricoRepository extends JpaRepository<HistoricoTarefa, UUID> {


    @Query(value = "select * from historico_tarefas where gerente_id = :gerenteId ", nativeQuery = true)
    List<HistoricoTarefa> findAllTarefasByGerente(@Param("gerenteId") UUID gerenteId) ;

    @Query(value = "select * from historico_tarefas where executor_id = :executorId", nativeQuery = true)
    List<HistoricoTarefa> findByExecutorIdNative(@Param("executorId") UUID executorId);

    @Query(value = "select * from historico_tarefas where tarefa_id = :tarefaId", nativeQuery = true)
    List<HistoricoTarefa> findByTarefaIdNative(@Param("tarefaId") UUID tarefaId);

   /* @Query("SELECT ht FROM HistoricoTarefa ht WHERE ht.executor.id =:executorId and ht.tarefa.id =:tarefaId")
    Optional<List<HistoricoTarefa>> findHistoricoByExecutorIdAndTarefaId(
            @Param("executorId") UUID executorId,
            @Param("tarefaId") UUID tarefaId
    );*/

  /*  @Query("SELECT ht FROM HistoricoTarefa ht WHERE ht.gerente.id =:gerenteId")
    Optional<List<HistoricoTarefa>> findHistoricoByGerenteId(
            @Param("gerenteId") UUID gerenteId
    );*/

/*
    @Query("SELECT ht FROM HistoricoTarefa ht WHERE ht.gerente.id =:gerenteId AND atrasada = true")
    Optional<List<HistoricoTarefa>> findHistoricoByGerenteIdAtrasadas(
            @Param("gerenteId") UUID gerenteId
    );*/
}
