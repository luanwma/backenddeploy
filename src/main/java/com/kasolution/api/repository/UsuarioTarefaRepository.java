package com.kasolution.api.repository;

import com.kasolution.api.model.TarefaStatus;
import com.kasolution.api.model.UsuarioTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioTarefaRepository extends JpaRepository<UsuarioTarefa, Long> {

    @Query("select ut from UsuarioTarefa ut where ut.tarefa.id = :tarefaId and ut.executor = :usuarioId")
    Optional<UsuarioTarefa> findUsuarioTarefaByTarefaAndUsuarioId(@Param("tarefaId") UUID tarefaId, @Param("usuarioId") UUID usuarioId);


    Optional<UsuarioTarefa> findByTarefaIdAndExecutorId(@Param("tarefaId") UUID tarefaId ,
                                                        @Param("executor") UUID executor);

    @Query("select ut from UsuarioTarefa ut where ut.tarefa.id = :tarefaId ")
    Optional<List<UsuarioTarefa>> findAllByTarefaId(@Param("tarefaId") UUID tarefaId);


    boolean existsByExecutor_IdAndTarefa_TarefaStatus(UUID executorId, TarefaStatus status);

    Optional<UsuarioTarefa> findByTarefa_IdAndExecutor_Id(UUID tarefaId, UUID executorId);
}

