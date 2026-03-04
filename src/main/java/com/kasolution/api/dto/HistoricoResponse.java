package com.kasolution.api.dto;

import com.kasolution.api.model.HistoricoTarefa;

import java.time.Instant;
import java.util.UUID;

public record HistoricoResponse(
        UUID id,
        UUID executorId,
        String nomeExecutor,
        UUID tarefaId,
        String nomeTarefa,
        Long totalTempoSegundos,
        Instant prazoDado,
        Instant iniciadaEm,
        Instant finalizadaEm,
        String detalhesExecucao


) {


    // Construtor que recebe a Entidade e mapeia para o Record
    public HistoricoResponse(HistoricoTarefa historico) {
        this(
                historico.getId(),
                historico.getExecutorId(),
                historico.getNomeExecutor(),
                historico.getTarefaId(),
                historico.getNomeTarefa(),
                historico.getTotalTempoSegundos(),
                historico.getPrazoDado(),
                historico.getIniciadaEm(),
                historico.getFinalizadaEm(),
                historico.getDetalhesExecucao()
        );
    }
}
