package com.kasolution.api.repository;

import com.kasolution.api.model.PrioridadeType;
import com.kasolution.api.model.Tarefa;
import com.kasolution.api.model.TarefaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository extends JpaRepository<Tarefa, UUID> {



    @Query("SELECT t FROM Tarefa t where t.id = :tarefaId and t.active = true")
    Optional<Tarefa> findTarefaById(@Param("tarefaId") UUID tarefaId);

  //  @Query("Select t from tarefas t where t.gerente.id")

    List<Tarefa> findByGerenteId(UUID gerenteId);


    @Query("SElECT t FROM Tarefa t WHERE t.gerente.id =:gerenteid AND t.active = true")
    List<Tarefa> findTarefaAtivaByGerenteId(@Param("gerenteid") UUID gerenteid);



    @Query("SELECT t FROM Tarefa t WHERE t.active = true " +
            "AND (:status IS NULL OR t.tarefaStatus = :status) " +
            "AND (:prioridade IS NULL OR t.prioridade = :prioridade)")
    List<Tarefa> buscarTarefasAtivasComFiltros(
            @Param("status") TarefaStatus status,
            @Param("prioridade") PrioridadeType prioridade
    );

}
