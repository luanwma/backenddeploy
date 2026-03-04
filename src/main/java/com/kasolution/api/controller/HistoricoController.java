package com.kasolution.api.controller;

import com.kasolution.api.dto.HistoricoResponse;
import com.kasolution.api.exception.CredenciaisException;
import com.kasolution.api.model.HistoricoTarefa;
import com.kasolution.api.model.Role;
import com.kasolution.api.repository.HistoricoRepository;
import com.kasolution.api.security.UsuarioAutenticado;
import com.kasolution.api.service.HistoricoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/historico")
@RequiredArgsConstructor
public class HistoricoController {


    private final HistoricoRepository historicoRepository;
    private final HistoricoService historicoService;

    @GetMapping("/gerente")
    public ResponseEntity<List<HistoricoResponse>> historicoCompleto(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ){
        if(usuarioAutenticado.role() != Role.GERENTE){
            throw new CredenciaisException("Usuário logado não é gerente");
        }
        UUID gerenteId = usuarioAutenticado.id();

        System.out.println("gerente id "+gerenteId);

        List<HistoricoTarefa> historicoDb = historicoService.buscarHistoricoCompleto(gerenteId);
        System.out.println("dados "+historicoDb);
        List<HistoricoResponse> response = historicoDb.stream()
                .map(HistoricoResponse::new)
                .toList();


        return ResponseEntity.ok(response);
    }

    @GetMapping("/desenvolvedor/{executorId}")
    public ResponseEntity<List<HistoricoResponse>> historicoExecutor(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
            @PathVariable UUID executorId

            ){

        if(usuarioAutenticado.role() != Role.GERENTE){
            throw new CredenciaisException("Usuário logado não é gerente");
        }

        List<HistoricoTarefa> historicoDb = historicoService.buscarHistoricoExecutor(executorId);
        List<HistoricoResponse> response = historicoDb.stream()
                .map(HistoricoResponse::new)
                .toList();


        return ResponseEntity.ok(response);

    }

    @GetMapping("/tarefa/{tarefaId}")
    public ResponseEntity<List<HistoricoResponse>> historicoTarefa(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
            @PathVariable UUID tarefaId

    ){

        if(usuarioAutenticado.role() != Role.GERENTE){
             throw new CredenciaisException("Usuário logado não é gerente");
        }

        List<HistoricoTarefa> historicoDb = historicoService.buscarHistoricoTarefa(tarefaId);
        List<HistoricoResponse> response = historicoDb.stream()
                .map(HistoricoResponse::new)
                .toList();


        return ResponseEntity.ok(response);

    }





}
