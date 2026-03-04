package com.kasolution.api.service;


import com.kasolution.api.dto.UsuarioRequest;
import com.kasolution.api.dto.UsuarioResponse;
import com.kasolution.api.exception.CredenciaisException;
import com.kasolution.api.model.Role;
import com.kasolution.api.model.Usuario;
import com.kasolution.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {


    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UsuarioResponse registrarGerente(UsuarioRequest usuarioRequest){
        if(usuarioRepository.existsByEmail(usuarioRequest.email())){
            throw new CredenciaisException("Ja existe um cadastro com esse email");
        }
        String password = passwordEncoder.encode(usuarioRequest.senha());
        Usuario gerente = new Usuario().builder()
                .nome(usuarioRequest.nome())
                .email(usuarioRequest.email())
                .senha(password)
                .role(Role.GERENTE).
                build();
        gerente = usuarioRepository.save(gerente);

        return UsuarioResponse.builder()
                .id(gerente.getId())
                .nome(gerente.getNome())
                .email(gerente.getEmail())
                .role(gerente.getRole())
                .ativo(gerente.isActive())
                .build();

    }


}
