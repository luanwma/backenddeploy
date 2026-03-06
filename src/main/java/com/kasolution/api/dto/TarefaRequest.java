package com.kasolution.api.dto;

import com.kasolution.api.model.PrioridadeType;
import com.kasolution.api.model.TarefaStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.time.Instant;
import java.util.Set;


@Builder
public record TarefaRequest(

        @NotBlank(message = "O Titulo é obrigatório") String titulo,

       @NotBlank(message = "A descricao é obrigatório") String descricao,

      // @NotNull(message = "O status da tarefa é obrigatório")TarefaStatus tarefaStatus,
        TarefaStatus tarefaStatus ,

       @NotNull(message = "O prazo final é obrigatório")Instant prazoFinal ,

       @NotNull(message = "A Prioridade é obrigatório") PrioridadeType prioridade,

       @NotNull(message = "Categoria é obrigatória")
       @Valid // Garante que as validações do CategoriaRequest sejam executadas
       CategoriaRequest categoria,

       Set<String> etiquetas

) {

    public TarefaRequest{

        tarefaStatus = TarefaStatus.TODO;

        if (etiquetas == null) {
            etiquetas = java.util.Collections.emptySet();
        }
    }

}


