package com.kasolution.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "usuario_tarefa" , uniqueConstraints = {
    @UniqueConstraint( columnNames = {"tarefa_id", "executor"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioTarefa {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor", nullable = false)
    private Usuario executor;
   /* // retirar o status daqui e manter apenas em tarefa
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TarefaStatus status = TarefaStatus.DOING;*/

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "paused_at")
    private Instant pausedAt;

    @Column(name = "last_started_at")
    private Instant lastStartedAt;

    @Column(name = "last_paused_at")
    private Instant lastPausedAt;

   /* @Column(name = "finished_at")
    private Instant finishedAt;*/


    // NOVO CAMPO: Armazena a soma de todos os intervalos já finalizados/pausados
    @Column(name = "segundos_trabalhados")
    @Builder.Default
    private Long segundosTrabalhados = 0L;

    @Column(name = "detalhes_execucao")
    private String detalhesExecucao;

    public Duration getTempoTotalGasto() {
        Duration totalAcumulado = Duration.ofSeconds(this.segundosTrabalhados != null ? this.segundosTrabalhados : 0);

        if (this.tarefa.getTarefaStatus() == TarefaStatus.DOING && this.lastStartedAt != null) {
            // Soma o tempo "ao vivo" (live tracking)
            Duration tempoSessaoAtual = Duration.between(this.lastStartedAt, Instant.now());
            return totalAcumulado.plus(tempoSessaoAtual);
        }

        return totalAcumulado;
    }




    /**
     * Apenas registra o início do relógio para ESTE usuário.
     * Não altera o status da Tarefa aqui!
     */
    public void registrarInicioSessao() {
        Instant agora = Instant.now();
        if (this.startedAt == null) {
            this.startedAt = agora;
        }
        this.lastStartedAt = agora;
    }

    /**
     * Apenas trava o relógio para ESTE usuário e acumula o tempo.
     */
    public void registrarPausaOuFimSessao() {
        if (this.lastStartedAt != null) {
            Instant agora = Instant.now();
            Duration sessaoAtual = Duration.between(this.lastStartedAt, agora);
            this.segundosTrabalhados += sessaoAtual.getSeconds();

            this.lastPausedAt = agora;
            this.lastStartedAt = null; // Reseta o cronômetro ativo
        }
    }


}
