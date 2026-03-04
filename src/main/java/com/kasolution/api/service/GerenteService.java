package com.kasolution.api.service;

import com.kasolution.api.dto.UsuarioRequest;
import com.kasolution.api.dto.UsuarioResponse;
import com.kasolution.api.exception.CredenciaisException;
import com.kasolution.api.model.Role;
import com.kasolution.api.model.Usuario;
import com.kasolution.api.repository.TarefaRepository;
import com.kasolution.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GerenteService {


    private final UsuarioRepository usuarioRepository;

    private final TarefaRepository tarefaRepository;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UsuarioResponse registroGerente(UsuarioRequest request){

        if(usuarioRepository.existsByEmail(request.email())){
            throw new CredenciaisException("Já existe um usuario cadastrado com esse email");
        }
        String password = passwordEncoder.encode(request.senha());


        Usuario user = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(password)
                .role(Role.GERENTE)
                .active(true)
                .build();

        user = usuarioRepository.save(user);
        return  UsuarioResponse.builder()
                .id(user.getId())
                .nome(user.getNome())
                .email(user.getEmail())
                .role(user.getRole())
                .ativo(user.isActive())
                .build();

    }


    @Transactional
    public UsuarioResponse registroDesenvolvedor(UsuarioRequest request, UUID gerenteId){

        if(usuarioRepository.existsByEmail(request.email())){
            throw new CredenciaisException("Já existe um usuario cadastrado com esse email");
        }
        String password = passwordEncoder.encode(request.senha());


        Usuario user = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(password)
                .role(Role.DESENVOLVEDOR)
                .gerenteid(gerenteId)
                .active(true)
                .build();

        user = usuarioRepository.save(user);
        return  UsuarioResponse.builder()
                .id(user.getId())
                .nome(user.getNome())
                .email(user.getEmail())
                .role(user.getRole())
                .gerenteid(user.getGerenteid())
                .ativo(user.isActive())
                .build();

    }






}
