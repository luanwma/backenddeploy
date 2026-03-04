package com.kasolution.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tarefas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    @NotBlank
    @Column(nullable = false)
    private String titulo;



    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TarefaStatus tarefaStatus = TarefaStatus.TODO;


    @Column(name = "prazo_final")
    private Instant prazoFinal;

   @Column(name = "finished_at")
    private Instant finishedAt;

    // NOVO CAMPO: O dono/criador da tarefa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gerente_id", nullable = false, updatable = false)
    private Usuario gerente;

    @Builder.Default
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    PrioridadeType prioridade = PrioridadeType.NORMAL;

    public boolean isAtrasada() {
        if (this.prazoFinal == null) {
            return false;
        }

        // Se a tarefa não está DONE, verifica se o momento de AGORA já passou do prazo
        if (this.tarefaStatus != TarefaStatus.DONE) {
            return Instant.now().isAfter(this.prazoFinal);
        }

        return false;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToMany
    @JoinTable(
            name = "tarefa_etiqueta",
            joinColumns = @JoinColumn(name = "tarefa_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    @Builder.Default
    private Set<Etiqueta> etiquetas = new HashSet<>();



    public void finalizarTarefa(){
        this.finishedAt = Instant.now();
    }
  /*  @Column(name = "avaliacao_gerente")
    private String avaliacaoGerente;*/






}
