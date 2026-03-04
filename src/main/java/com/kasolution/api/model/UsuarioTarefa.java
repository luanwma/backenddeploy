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
/*

    public void iniciar() {
        Instant agora = Instant.now();

        if (this.startedAt == null) {
            this.startedAt = agora;
        }

        this.lastStartedAt = agora;
        this.tarefa.setTarefaStatus(TarefaStatus.DOING);
    }

    */
/**
     * Pausa a tarefa e acumula o tempo trabalhado na sessão atual
     *//*

    public void pausar() {
        if (this.tarefa.getTarefaStatus() != TarefaStatus.DOING || this.lastStartedAt == null) {
            throw new IllegalStateException("Não é possível pausar uma tarefa que não está em andamento.");
        }
        this.tarefa.setTarefaStatus(TarefaStatus.PAUSED);

        acumularTempo();
        this.lastStartedAt = null; // Reseta o início da sessão atual pois pausou
        this.tarefa.setTarefaStatus(TarefaStatus.PAUSED);
    }


    public void review(){
        if(this.tarefa.getTarefaStatus() == TarefaStatus.DOING){
            acumularTempo();
        }
        this.lastStartedAt = null;
        this.tarefa.setTarefaStatus(TarefaStatus.REVIEW);
        this.tarefa.setFinishedAt(Instant.now()); // o tempo de desenvolvimento termina aqui
    }

    */
/**
     * Finaliza a tarefa e acumula o último pedaço de tempo
     *//*

    public void finalizar() {
        */
/*if (this.status == TarefaStatus.DOING) {
            acumularTempo();
        }*//*

        if(this.tarefa.getTarefaStatus() != TarefaStatus.REVIEW){
            throw new IllegalStateException("Não é possível finalizar uma tarefa que não está na etapa de review.");
        }

        //this.finishedAt = Instant.now();
        //this.lastStartedAt = null;
        // Atualiza a Tarefa com o status e a data final!
        this.tarefa.setTarefaStatus(TarefaStatus.DONE);
        //this.tarefa.setFinishedAt(Instant.now());

    }

    */
/**
     * Método auxiliar privado para somar o tempo
     *//*

    private void acumularTempo() {
        if(this.lastStartedAt != null){
            Instant agora = Instant.now();
            Duration sessaoAtual = Duration.between(this.lastStartedAt, agora);
            this.segundosTrabalhados += sessaoAtual.getSeconds();
            this.lastPausedAt = agora;
        }

    }
*/

    /**
     * Calcula o tempo total dinamicamente.
     * Se estiver PAUSADO/FINALIZADO: Retorna o acumulado.
     * Se estiver DOING: Retorna o acumulado + o tempo decorrido desde o último start até AGORA.
     */
    public Duration getTempoTotalGasto() {
        Duration totalAcumulado = Duration.ofSeconds(this.segundosTrabalhados != null ? this.segundosTrabalhados : 0);

        if (this.tarefa.getTarefaStatus() == TarefaStatus.DOING && this.lastStartedAt != null) {
            // Soma o tempo "ao vivo" (live tracking)
            Duration tempoSessaoAtual = Duration.between(this.lastStartedAt, Instant.now());
            return totalAcumulado.plus(tempoSessaoAtual);
        }

        return totalAcumulado;
    }


    // testando nova abordagem para deixar tarefa service responsavel pelas mudanças de status

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
