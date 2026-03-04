package com.kasolution.api.controller;

import com.kasolution.api.dto.TarefaRequest;
import com.kasolution.api.dto.TarefaResponse;
import com.kasolution.api.exception.AccessDeniedException;
import com.kasolution.api.exception.CredenciaisException;
import com.kasolution.api.model.PrioridadeType;
import com.kasolution.api.model.Role;
import com.kasolution.api.model.Tarefa;
import com.kasolution.api.model.TarefaStatus;
import com.kasolution.api.security.UsuarioAutenticado;
import com.kasolution.api.service.TarefaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tarefas/")
@RequiredArgsConstructor
public class TarefaController {


    private final TarefaService tarefaService;

    @PostMapping("/criar")
    public ResponseEntity<TarefaResponse> criaTarefa(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestBody @Valid TarefaRequest tarefaRequest
    ){
        if(usuario.role() == Role.GERENTE){
            return ResponseEntity.ok(tarefaService.registroTarefa(usuario, tarefaRequest));
        }
        throw new CredenciaisException("Usuario logado não é gerente");

    }

    @PutMapping("/editar/{tarefaId}")
    public ResponseEntity<TarefaResponse> editarTarefa(
            @PathVariable UUID tarefaId,
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestBody @Valid TarefaRequest tarefaRequest
    ){
        if(usuario.role() != Role.GERENTE){
            throw new AccessDeniedException("Somente o gerente pode alterar determinadas caracteristicas das tarefas");
        }
        Tarefa t  = tarefaService.editarTarefa(tarefaId, usuario, tarefaRequest);

        return ResponseEntity.ok(TarefaResponse.builder()
                .id(t.getId())
                .titulo(t.getTitulo())
                .descricao(t.getDescricao())
                .tarefaStatus(t.getTarefaStatus())
                .prazoFinal(t.getPrazoFinal())
                .build()
        );

    }

    @DeleteMapping("/deletar/{tarefaId}/")
    public ResponseEntity<Void> deletarTarefa(
            @PathVariable UUID tarefaId,
            @AuthenticationPrincipal UsuarioAutenticado usuario
           // @RequestParam(required = false, defaultValue = "false") boolean confirmada

    ){
        if(usuario.role() == Role.GERENTE){
            tarefaService.deletarTarefa(tarefaId, usuario);
            //return ResponseEntity.noContent().build();(tarefaService.deletarTarefa(tarefaId,usuario, confirmada ));
            return ResponseEntity.noContent().build();
        }
        throw new CredenciaisException("Usuario logado não é gerente");

    }


    @GetMapping("/dashboard")
    public ResponseEntity<List<TarefaResponse>> dashboard(
            @AuthenticationPrincipal UsuarioAutenticado usuario

    ){

        List<TarefaResponse> tarefasAtivas = tarefaService.buscarTarefasAtivas(usuario);
        return ResponseEntity.ok(tarefasAtivas);
    }


    @GetMapping("/filtro")
    public ResponseEntity<List<TarefaResponse>> aplicarFiltro(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestParam(required = false) TarefaStatus status,
            @RequestParam(required = false)PrioridadeType prioridade
            ){

        List<TarefaResponse> tarefasdb = tarefaService.buscarTarefasAtivas(usuario, status, prioridade);

        return ResponseEntity.ok(tarefasdb); // Retorna 200 OK com o corpo
    }


}
