package com.kasolution.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "historico_tarefas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoTarefa {



    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false, name = "gerente_id")
    private UUID gerenteId; // vem de gerente tarefa


    @Column(nullable = false, name = "executor_id")
    private UUID executorId; // vem do usuariotarefa

    @Column(nullable = false, name = "nome_executor")
    private String nomeExecutor;

    @Column(nullable = false, name = "tarefa_id")
    private UUID tarefaId;

    @Column(name = "nome_tarefa", nullable = false)
    private String nomeTarefa;

    @Column(name = "total_tempo_segundos", nullable = false)
    private Long totalTempoSegundos;

    @Column(nullable = false, name = "prazo_dado")
    private Instant prazoDado;


    @Column(name = "iniciada_em")
    private Instant iniciadaEm;

    @Column(name = "finalizada_em")
    private Instant finalizadaEm;

    @Column(nullable = false)
    private boolean atrasada;

    @Column(name = "detalhes_execucao")
    private String detalhesExecucao;




}
