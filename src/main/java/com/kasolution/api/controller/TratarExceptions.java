package com.kasolution.api.controller;

import com.kasolution.api.dto.ErroResponse;
import com.kasolution.api.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class TratarExceptions {

    @ExceptionHandler(TarefaExclusionException.class)
    public ResponseEntity<ErroResponse> tratarTarefaExclusionException(TarefaExclusionException ex, HttpServletRequest request) {

        // Status 409 Conflict é ótimo para regras de negócio que impedem uma ação
        HttpStatus status = HttpStatus.CONFLICT;

        ErroResponse erro = new ErroResponse(
                Instant.now(),
                status.value(),
                "Conflito com as regras definidas para tarefas",
                ex.getMessage(), // Aqui vai a mensagem que você passou no throw new RegraNegocioException(...)
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> tratarAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {

        // Status 409 Conflict é ótimo para regras de negócio que impedem uma ação
        HttpStatus status = HttpStatus.FORBIDDEN;

        ErroResponse erro = new ErroResponse(
                Instant.now(),
                status.value(),
                "Acesso negado",
                ex.getMessage() != null ? ex.getMessage() : "Você não tem permissão para acessar este recurso.",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErroResponse> tratarEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {

        // Status 409 Conflict é ótimo para regras de negócio que impedem uma ação
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErroResponse erro = new ErroResponse(
                Instant.now(),
                status.value(),
                "Não encontrado",
                ex.getMessage() != null ? ex.getMessage() : "O registro solicitado não existe no banco de dados.",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }
    @ExceptionHandler(CredenciaisException.class)
    public ResponseEntity<ErroResponse> tratarCredenciaisException(CredenciaisException ex, HttpServletRequest request) {

        // Status 409 Conflict é ótimo para regras de negócio que impedem uma ação
        HttpStatus status = HttpStatus.FORBIDDEN;

        ErroResponse erro = new ErroResponse(
                Instant.now(),
                status.value(),
                "Credenciais inválidas",
                ex.getMessage() != null ? ex.getMessage() : "As credenciais como email e/ou senha estão inválidas",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarErroDeIntegridade(DataIntegrityViolationException ex, HttpServletRequest request) {

        // status 409 (conflict) ou 400 (Bad Request) cabem bem aqui. vou usar 409.
        HttpStatus status = HttpStatus.CONFLICT;

        // Opcional: tentar extrair uma mensagem mais amigável do erro complexo do banco
        String mensagemAmigavel = "Erro de integridade de dados. Verifique se todos os campos obrigatórios foram preenchidos corretamente ou se já não existe um registro duplicado (ex: email).";

        ErroResponse erro = new ErroResponse(
                Instant.now(),
                status.value(),
                "Violação de Integridade do Banco de Dados",
                mensagemAmigavel,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }


    @ExceptionHandler(TarefaStatusException.class)
    public ResponseEntity<ErroResponse> tratarTarefaStatusException(TarefaStatusException ex, HttpServletRequest request) {


        // Status 409 Conflict é ótimo para regras de negócio que impedem uma ação
        HttpStatus status = HttpStatus.CONFLICT;

        ErroResponse erro = new ErroResponse(
                Instant.now(),
                status.value(),
                "Conflito com as regras definidas para tarefas",
                ex.getMessage(), // Aqui vai a mensagem que você passou no throw new RegraNegocioException(...)
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

  /*  @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarErroDeTokenExpired(ExpiredJwtException ex, HttpServletRequest request) {

        // Status 409 (Conflict) ou 400 (Bad Request) cabem bem aqui. Vamos usar 409.
        HttpStatus status = HttpStatus.CONFLICT;

        // Opcional: tentar extrair uma mensagem mais amigável do erro complexo do banco
        String mensagemAmigavel = "Erro de integridade de dados. Verifique se todos os campos obrigatórios foram preenchidos corretamente ou se já não existe um registro duplicado (ex: email).";

        ErroResponse erro = new ErroResponse(
                Instant.now(),
                status.value(),
                "Violação de Integridade do Banco de Dados",
                mensagemAmigavel,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }*/

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErroResponse> tratarExpiredJwtException(ExpiredJwtException ex, HttpServletRequest request) {

        // Status 409 Conflict é ótimo para regras de negócio que impedem uma ação
        HttpStatus status = HttpStatus.FORBIDDEN;

        ErroResponse erro = new ErroResponse(
                Instant.now(),
                status.value(),
                "Token Expirado!",
                ex.getMessage() != null ? ex.getMessage() : "As credenciais como email e/ou senha estão inválidas",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }


    @ExceptionHandler(DataNotFound.class)
    public ResponseEntity<ErroResponse> tratarDataNotFound(DataNotFound ex, HttpServletRequest request) {


        HttpStatus status = HttpStatus.NOT_FOUND;

        ErroResponse erro = new ErroResponse(
                Instant.now(),
                status.value(),
                "Dado não encontrado",
                ex.getMessage() != null ? ex.getMessage() : "Dado não encontrado no banco de dados",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }


}
