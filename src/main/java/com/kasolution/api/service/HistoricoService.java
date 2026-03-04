package com.kasolution.api.service;

import com.kasolution.api.dto.HistoricoResponse;
import com.kasolution.api.exception.DataNotFound;
import com.kasolution.api.model.HistoricoTarefa;
import com.kasolution.api.model.Tarefa;
import com.kasolution.api.model.Usuario;
import com.kasolution.api.model.UsuarioTarefa;
import com.kasolution.api.repository.HistoricoRepository;
import com.kasolution.api.repository.TarefaRepository;
import com.kasolution.api.repository.UsuarioRepository;
import com.kasolution.api.repository.UsuarioTarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final HistoricoRepository historicoRepository;
    private final TarefaRepository tarefaRepository;
    private final UsuarioTarefaRepository usuarioTarefaRepository;
    private final UsuarioRepository usuarioRepository;


    @Transactional
    public boolean registrarHistorico(Tarefa tarefa){
        List<UsuarioTarefa> registros = usuarioTarefaRepository.findAllByTarefaId(tarefa.getId())
                .orElseThrow( () -> new DataNotFound("Registros nao encontrados"));

        // 2. Percorrer a lista usando Stream para criar os objetos de Histórico
        List<HistoricoTarefa> listaHistoricos = registros.stream().map(ut -> {


            HistoricoTarefa historico = new HistoricoTarefa();


            historico.setTarefaId(tarefa.getId());
            historico.setNomeTarefa(tarefa.getTitulo());
            historico.setGerenteId(tarefa.getGerente().getId());
            historico.setPrazoDado(tarefa.getPrazoFinal());
            historico.setAtrasada(tarefa.isAtrasada());


            historico.setDetalhesExecucao(ut.getDetalhesExecucao());


            historico.setExecutorId(ut.getExecutor().getId());
            historico.setNomeExecutor(ut.getExecutor().getNome());

            historico.setIniciadaEm(ut.getStartedAt() );
            historico.setFinalizadaEm(tarefa.getFinishedAt());
            historico.setTotalTempoSegundos(ut.getSegundosTrabalhados());

            return historico;

        }).toList();


        List<HistoricoTarefa> listRegistros = historicoRepository.saveAll(listaHistoricos);
        return !listRegistros.isEmpty();
    }


    @Transactional(readOnly = true)
    public List<HistoricoTarefa> buscarHistoricoCompleto(UUID gerenteId){

        return historicoRepository.findAllTarefasByGerente(gerenteId);

    }


    @Transactional(readOnly = true)
    public  List<HistoricoTarefa> buscarHistoricoTarefa(UUID tarefaId){
        Tarefa t = tarefaRepository.findById(tarefaId).orElseThrow( () ->
                new DataNotFound("Tarefa não encontrada"));

        return historicoRepository.findByTarefaIdNative(tarefaId);
    }

    @Transactional(readOnly = true)
    public  List<HistoricoTarefa> buscarHistoricoExecutor(UUID executorId){
        Usuario u =usuarioRepository.findById(executorId).orElseThrow( () ->
               new DataNotFound("Nao foi encontrado o usuario")
        );
        return historicoRepository.findByExecutorIdNative(executorId);
    }




}
