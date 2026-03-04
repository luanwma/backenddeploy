package com.kasolution.api.service;

import com.kasolution.api.dto.*;
import com.kasolution.api.exception.*;
import com.kasolution.api.model.*;
import com.kasolution.api.repository.*;
import com.kasolution.api.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    private final UsuarioRepository usuarioRepository;

    private final CategoriaRepository categoriaRepository;

    private final EtiquetaRepository etiquetaRepository;

    private final HistoricoService historicoService;


    // para testar nova abordagem

    private final UsuarioTarefaRepository usuarioTarefaRepository;

    @Transactional
    public TarefaResponse registroTarefa(UsuarioAutenticado gerente, TarefaRequest request){

        Usuario gerenteRef = usuarioRepository.getReferenceById(gerente.id());




        Categoria cat = categoriaRepository.findByNome(request.categoria().nome())
                .orElseGet( () ->{
                    Categoria newcat = Categoria.builder()
                            .nome(request.categoria().nome())
                            .hexadecimal(request.categoria().hexadecimal())
                            .build();
                    return categoriaRepository.save(newcat);
                });


        // 2. Resolve as Etiquetas (Busca ou Cria para cada nome da lista)
        Set<Etiqueta> etiquetasParaSalvar = new HashSet<>();
        if (request.etiquetas() != null && !request.etiquetas().isEmpty()) {
            etiquetasParaSalvar = request.etiquetas().stream()
                    .map(nomeEtiqueta -> etiquetaRepository.findByDescricao(String.valueOf(nomeEtiqueta))
                            .orElseGet(() -> {
                                Etiqueta novaEtiqueta = new Etiqueta();
                                novaEtiqueta.setDescricao(String.valueOf(nomeEtiqueta));
                                return etiquetaRepository.save(novaEtiqueta);
                            })
                    ).collect(Collectors.toSet());
        }
        Tarefa novaTarefa = Tarefa.builder()
                .titulo(request.titulo())

                .descricao(request.descricao())
                .tarefaStatus(request.tarefaStatus())
                .prazoFinal(request.prazoFinal())
                .gerente(gerenteRef)
                .prioridade(request.prioridade())
                .categoria(cat)
                .etiquetas(etiquetasParaSalvar)
                .build();


        novaTarefa = tarefaRepository.save(novaTarefa);
        CategoriaResponse catResponse = new CategoriaResponse(
                novaTarefa.getCategoria().getId(),
                novaTarefa.getCategoria().getNome(),
                novaTarefa.getCategoria().getHexadecimal()
        );
        // 2. Mapeia o Set de Etiquetas salvas para um Set de DTOs
        Set<EtiquetaResponse> etiquetasResponse = novaTarefa.getEtiquetas().stream()
                .map(tag -> new EtiquetaResponse(tag.getId(), tag.getDescricao()))
                .collect(Collectors.toSet());
        // 3. Monta o TarefaResponse final
        TarefaResponse response = TarefaResponse.builder()
                .id(novaTarefa.getId())
                .titulo(novaTarefa.getTitulo())
                .descricao(novaTarefa.getDescricao())
                .tarefaStatus(novaTarefa.getTarefaStatus())
                .prazoFinal(novaTarefa.getPrazoFinal())
                .prioridade(novaTarefa.getPrioridade())
                .categoria(catResponse)
                .etiquetas(etiquetasResponse)
                .build();
        return response;

    }


    @Transactional
    public Tarefa editarTarefa(UUID tarefaId, UsuarioAutenticado gerente,  TarefaRequest request){

        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));

       /* if (!tarefa.getGerente().getId().equals(gerente.id())) {
            throw new AccessDeniedException("Você não tem permissão para alterar esta tarefa.");
        }*/

        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());

        tarefa.setPrazoFinal(request.prazoFinal());

        tarefa = tarefaRepository.save(tarefa);
        return tarefa;

    }

    @Transactional
    public boolean deletarTarefa(UUID tarefaId, UsuarioAutenticado gerente){
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));

        if (!tarefa.getGerente().getId().equals(gerente.id())) {
            throw new AccessDeniedException("Você não tem permissão para deletar esta tarefa.");
        }

        if(tarefa.getTarefaStatus() == TarefaStatus.DONE){
            throw new TarefaExclusionException("Tarefa já foi concluida, portanto não pode ser excluida");
        }
        // REGRA DE NEGÓCIO: Se já começou e não tem a flag de confirmação, bloqueia!
        if (tarefa.getTarefaStatus() != TarefaStatus.TODO) {
            throw new TarefaExclusionException("Esta tarefa já possui progresso.");
        }

        tarefa.setActive(false);
        tarefa = tarefaRepository.save(tarefa);
        return historicoService.registrarHistorico(tarefa);
       // return finalizarTarefa(tarefaId, gerente, "Tarefa deletada");


    }

    @Transactional(readOnly = true)
    public List<TarefaResponse> buscarTarefasAtivas(UsuarioAutenticado usuario){
       // List<TarefaResponse> tarefas = new ArrayList<>();
        List<Tarefa> tarefasdb = tarefaRepository.findTarefaAtivaByGerenteId(usuario.gerenteId());

        return tarefasdb.stream()
                .map(tarefa -> new TarefaResponse(tarefa))
                .toList();

    }

    @Transactional(readOnly = true)
    public Tarefa buscarTarefaAtiva(UUID tarefaId){
        return tarefaRepository.findTarefaById(tarefaId)
                .orElseThrow( () ->  new DataNotFound("Tarefa não encontrada"));


    }



    // testado nova abordagem

    @Transactional
    public void iniciarTarefa(UUID tarefaId, UsuarioAutenticado usuario) {
        boolean jaTemTarefaEmAndamento = usuarioTarefaRepository
                .existsByExecutor_IdAndTarefa_TarefaStatus(usuario.id(), TarefaStatus.DOING);
        if (jaTemTarefaEmAndamento) {
            throw new TarefaStatusException("Você já possui uma tarefa em andamento. Pause-a ou finalize antes de iniciar outra.");
        }

        Tarefa t = tarefaRepository.findTarefaById(tarefaId)
                .orElseThrow( () ->  new DataNotFound("Tarefa não encontrada"));



        // 2. REGRA DE NEGÓCIO: Bloqueia se já estiver em andamento (por qualquer pessoa)
        if (t.getTarefaStatus() == TarefaStatus.DOING ) {

            throw new TarefaStatusException("Esta tarefa já está em andamento por outro desenvolvedor. Aguarde ela ser pausada.");
        }
        if (t.getTarefaStatus() == TarefaStatus.DONE) {
            throw new TarefaStatusException("Esta tarefa já está finalizada.");
        }

        Usuario u = usuarioRepository.findById(usuario.id())
                .orElseThrow( () -> new DataNotFound("Usuario nao encontrado no banco de dados"));
        UsuarioTarefa alocacao = usuarioTarefaRepository.findByTarefaIdAndExecutorId(tarefaId, usuario.id())
                .orElseGet( () -> criarNovoUsuarioTarefa(t, usuario.id()));

        alocacao.registrarInicioSessao();
        t.setTarefaStatus(TarefaStatus.DOING);
        usuarioTarefaRepository.save(alocacao);
        tarefaRepository.save(t);
    }

    @Transactional
    public void pausarTarefa(UUID tarefaId, UsuarioAutenticado usuario, String detalhesExecucao) {
        Tarefa t = buscarTarefa(tarefaId);


        if (t.getTarefaStatus() != TarefaStatus.DOING) {
            throw new TarefaStatusException("Esta tarefa não está em andamento");
        }
        Usuario u = usuarioRepository.findById(usuario.id())
                .orElseThrow( () -> new DataNotFound("Usuario nao encontrado no banco de dados"));
        UsuarioTarefa alocacao = usuarioTarefaRepository.findByTarefaIdAndExecutorId(tarefaId, usuario.id())
                .orElseThrow(() ->   new DataNotFound("Alocacao de usuario na tarefa nao existe no banco de dados"));

        alocacao.registrarPausaOuFimSessao(); // Pausa o relógio e acumula o tempo
        String texto = alocacao.getDetalhesExecucao();
        String novosDetalhes = texto+"|"+ detalhesExecucao;
        t.setTarefaStatus(TarefaStatus.PAUSED);
        alocacao.setDetalhesExecucao(novosDetalhes);
        // Sincroniza o status da Tarefa, apontando que o desenvolvimento parou
        //alocacao.getTarefa().setTarefaStatus(TarefaStatus.PAUSED);

        usuarioTarefaRepository.save(alocacao);
        tarefaRepository.save(t);
    }

    @Transactional
    public boolean finalizarTarefa(UUID tarefaId, UsuarioAutenticado usuario,String detalhesExecucao){

        Usuario u = usuarioRepository.findById(usuario.id())
                .orElseThrow( () -> new DataNotFound("Usuario nao encontrado no banco de dados"));
        UsuarioTarefa alocacao = usuarioTarefaRepository.findByTarefaIdAndExecutorId(tarefaId, usuario.id())
                .orElseThrow(() ->   new DataNotFound("Alocacao de usuario na tarefa nao existe no banco de dados"));

        alocacao.registrarPausaOuFimSessao(); // Pausa o relógio e acumula o tempo
        // alterar o metodo para finalizar o dado em finalizada_em está nulo
        Tarefa t = buscarTarefa(tarefaId);
        if(t.getTarefaStatus() == TarefaStatus.TODO){
            throw new TarefaStatusException("Tarefa precisa ser executada");
        }

        alocacao.getTarefa().setTarefaStatus(TarefaStatus.DONE);
        alocacao.setDetalhesExecucao(detalhesExecucao);
        t.finalizarTarefa();

        usuarioTarefaRepository.save(alocacao);
        tarefaRepository.save(t);

        return historicoService.registrarHistorico(t);

    }


    private UsuarioTarefa criarNovoUsuarioTarefa(Tarefa tarefa, UUID executor){
        Usuario dev = usuarioRepository.getReferenceById(executor);

        UsuarioTarefa ut = UsuarioTarefa.builder()
                .tarefa(tarefa)
                .executor(dev)
                .build();
        return ut;
    }

    private Tarefa buscarTarefa(UUID tarefaId){
        return tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new DataNotFound("Tarefa não encontrada"));
    }

    private UsuarioTarefa buscarUsuarioTarefa(UUID tarefaId, UUID usuarioId){
        return usuarioTarefaRepository.findUsuarioTarefaByTarefaAndUsuarioId(tarefaId, usuarioId)
                .orElseThrow(() -> new DataNotFound("Você não iniciou o apontamento desta tarefa ainda."));
    }



    // filtros

    @Transactional(readOnly = true)
    public List<TarefaResponse> buscarTarefasAtivas(UsuarioAutenticado usuario, TarefaStatus status, PrioridadeType prioridade) {

        return tarefaRepository.buscarTarefasAtivasComFiltros(status, prioridade)
                .stream()
                .map(TarefaResponse::new)
                .toList();
    }

}
