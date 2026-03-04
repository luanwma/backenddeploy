package com.kasolution.api.service;

import com.kasolution.api.exception.AccessDeniedException;
import com.kasolution.api.exception.DataNotFound;
import com.kasolution.api.exception.EntityNotFoundException;
import com.kasolution.api.exception.TarefaStatusException;
import com.kasolution.api.model.*;
import com.kasolution.api.repository.TarefaRepository;
import com.kasolution.api.repository.UsuarioRepository;
import com.kasolution.api.repository.UsuarioTarefaRepository;
import com.kasolution.api.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioTarefaService {

    private final UsuarioTarefaRepository usuarioTarefaRepository;

    private final TarefaRepository tarefaRepository;

    private final UsuarioRepository usuarioRepository;


    @Transactional
    public void iniciarTarefa(UUID tarefaId, UsuarioAutenticado usuario){

       /* Tarefa t = buscarTarefa(tarefaId);

        UsuarioTarefa usuarioTarefa = usuarioTarefaRepository.findByTarefaIdAndExecutorId(tarefaId, usuario.id())
                .orElseGet( () -> criarNovoUsuarioTarefa(t, usuario.id()));


        System.out.println("Chegou aqui encontrou o usuario tarefa "+usuarioTarefa);
        usuarioTarefa.registrarInicioSessao();
        t.setTarefaStatus(TarefaStatus.DOING);
        usuarioTarefaRepository.save(usuarioTarefa);
        tarefaRepository.save(t);
*/


    }

    @Transactional
    public void pausarTarefa(UUID tarefaId, UsuarioAutenticado usuario) {
      /*  Tarefa t = buscarTarefa(tarefaId);
        UsuarioTarefa usuarioTarefa = usuarioTarefaRepository.findByTarefaIdAndExecutorId(tarefaId, usuario.id())
                .orElseThrow(() ->   new DataNotFound("Alocacao de usuario na tarefa nao existe no banco de dados"));

        usuarioTarefa.registrarPausaOuFimSessao(); // Pausa o relógio e acumula o tempo

        // Sincroniza o status da Tarefa, apontando que o desenvolvimento parou
        usuarioTarefa.getTarefa().setTarefaStatus(TarefaStatus.PAUSED);

        usuarioTarefaRepository.save(usuarioTarefa);*/
    }


    @Transactional
    public void enviarTarefaReview(UUID tarefaId, UsuarioAutenticado usuario, String detalhesExecucao) {
/*
        Tarefa t = buscarTarefa(tarefaId);
        UsuarioTarefa usuarioTarefa = usuarioTarefaRepository.findByTarefaIdAndExecutorId(tarefaId, usuario.id())
                .orElseThrow(() ->   new DataNotFound("Alocacao de usuario na tarefa nao existe no banco de dados"));

        usuarioTarefa.registrarPausaOuFimSessao(); // Para o relógio, acumula o tempo e muda status interno
        t.setDetalhesExecucao(detalhesExecucao);
        t.setTarefaStatus(TarefaStatus.REVIEW);
        //usuarioTarefa.getTarefa().setTarefaStatus(TarefaStatus.REVIEW);


        usuarioTarefaRepository.save(usuarioTarefa);
        tarefaRepository.save(t);*/

    }

    @Transactional
    public void finalizarTarefa(UUID tarefaId, UsuarioAutenticado usuario,String detalhesExecucao) {

    /*    UsuarioTarefa usuarioTarefa = usuarioTarefaRepository.findByTarefaIdAndExecutorId(tarefaId, usuario.id())
                .orElseThrow(() ->   new DataNotFound("Alocacao de usuario na tarefa nao existe no banco de dados"));

        usuarioTarefa.registrarPausaOuFimSessao(); // Pausa o relógio e acumula o tempo

        Tarefa t = buscarTarefa(tarefaId);
        if(t.getTarefaStatus() != TarefaStatus.TODO){
            throw new TarefaStatusException("Tarefa precisa ser executada");
        }
        // Sincroniza o status da Tarefa, apontando que o desenvolvimento parou
        usuarioTarefa.getTarefa().setTarefaStatus(TarefaStatus.DONE);

        t.setDetalhesExecucao(detalhesExecucao);

        usuarioTarefaRepository.save(usuarioTarefa);
        tarefaRepository.save(t);

*/


    }








}
