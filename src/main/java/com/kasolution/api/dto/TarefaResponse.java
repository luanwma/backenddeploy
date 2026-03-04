package com.kasolution.api.dto;

import com.kasolution.api.model.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record TarefaResponse(
    UUID id,
    String titulo,
    String descricao,
    TarefaStatus tarefaStatus,
    Instant prazoFinal,
    PrioridadeType prioridade,
    CategoriaResponse categoria,
    Set<EtiquetaResponse> etiquetas

) {
    // Construtor que recebe a Entidade e extrai os dados
    // Construtor que recebe a Entidade Tarefa e extrai TODOS os dados
    public TarefaResponse(Tarefa tarefa) {
        this(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getTarefaStatus(),
                tarefa.getPrazoFinal(),
                tarefa.getPrioridade(),

                // Mapeando a Categoria (se existir)
                tarefa.getCategoria() != null ? new CategoriaResponse(tarefa.getCategoria()) : null,

                // Mapeando a lista de Etiquetas (se existir)
                tarefa.getEtiquetas() != null ?
                        tarefa.getEtiquetas().stream().map(EtiquetaResponse::new).collect(Collectors.toSet())
                        : null
        );
    }

}
