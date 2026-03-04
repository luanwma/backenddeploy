package com.kasolution.api.controller;


import com.kasolution.api.dto.TarefaRequest;
import com.kasolution.api.dto.TarefaResponse;
import com.kasolution.api.dto.UsuarioRequest;
import com.kasolution.api.dto.UsuarioResponse;
import com.kasolution.api.exception.CredenciaisException;
import com.kasolution.api.model.Role;
import com.kasolution.api.model.Tarefa;
import com.kasolution.api.security.UsuarioAutenticado;
import com.kasolution.api.service.GerenteService;
import com.kasolution.api.service.TarefaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/gerente")
@RequiredArgsConstructor
public class GerenteController {

    private final GerenteService gerenteService;

    private final TarefaService tarefaService;


    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registro(
            @RequestBody @Valid UsuarioRequest request
    ){
        UsuarioResponse user = gerenteService.registroGerente(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/registro/desenvolvedor")
    public ResponseEntity<UsuarioResponse> registroDesenvolvedor(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
            @RequestBody @Valid UsuarioRequest request

    ){
        if(usuarioAutenticado.role() != Role.GERENTE){
            throw new CredenciaisException("Usuario autenticado não tem permissão de cadastro de desenvolvedor");
        }
        UUID gerenteId = usuarioAutenticado.id();
        UsuarioResponse user = gerenteService.registroDesenvolvedor(request, gerenteId);
        return ResponseEntity.ok(user);

    }


   /*

   @PostMapping("/tarefas/criar")
    public ResponseEntity<TarefaResponse> criaTarefa(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestBody @Valid TarefaRequest tarefaRequest
            ){

        return ResponseEntity.ok(tarefaService.registroTarefa(usuario, tarefaRequest));

    }*/



}
