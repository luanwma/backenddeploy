package com.kasolution.api.controller;

import com.kasolution.api.dto.DetalhesExecucaoRequest;
import com.kasolution.api.dto.TarefaRequest;
import com.kasolution.api.dto.TarefaResponse;
import com.kasolution.api.exception.TarefaStatusException;
import com.kasolution.api.model.Tarefa;
import com.kasolution.api.model.TarefaStatus;
import com.kasolution.api.security.UsuarioAutenticado;
import com.kasolution.api.service.TarefaService;
import com.kasolution.api.service.UsuarioTarefaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
public class ExecucaoTarefaController {


    private final UsuarioTarefaService usuarioTarefaService;

    private final TarefaService tarefaService;


    @PostMapping("/{tarefaId}/iniciar")
    public ResponseEntity<Void> iniciarTarefa(
            @PathVariable UUID tarefaId,
            @AuthenticationPrincipal UsuarioAutenticado executor) {


        Tarefa t = tarefaService.buscarTarefaAtiva(tarefaId);

        if(t.getTarefaStatus() != TarefaStatus.TODO && t.getTarefaStatus() != TarefaStatus.PAUSED){
            throw new TarefaStatusException("A tarefa não esta disponivel");
        }

        tarefaService.iniciarTarefa(tarefaId, executor);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }

    @PostMapping("/{tarefaId}/pausar")
    public ResponseEntity<Void> pausarTarefa(
            @PathVariable UUID tarefaId,
            @AuthenticationPrincipal UsuarioAutenticado executor,
            @RequestBody DetalhesExecucaoRequest request) {
        Tarefa t = tarefaService.buscarTarefaAtiva(tarefaId);
        if(t.getTarefaStatus() != TarefaStatus.DOING ){
            throw new TarefaStatusException("A tarefa não esta disponivel");
        }
        tarefaService.pausarTarefa(tarefaId, executor,request.detalhesExecucao());
        return ResponseEntity.noContent().build();
    }



    @PostMapping("/{tarefaId}/finalizar")
    public ResponseEntity<Void> finalizarTarefa(
            @PathVariable UUID tarefaId,
            @AuthenticationPrincipal UsuarioAutenticado executor,
            @RequestBody DetalhesExecucaoRequest request) {
        Tarefa t = tarefaService.buscarTarefaAtiva(tarefaId);
        if(t.getTarefaStatus() != TarefaStatus.DOING && t.getTarefaStatus() != TarefaStatus.PAUSED){
            throw new TarefaStatusException("A tarefa não esta disponivel");
        }
        tarefaService.finalizarTarefa(tarefaId, executor, request.detalhesExecucao());
        return ResponseEntity.noContent().build();
    }



}
