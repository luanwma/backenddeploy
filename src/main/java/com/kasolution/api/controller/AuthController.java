package com.kasolution.api.controller;

import com.kasolution.api.dto.AuthRequest;
import com.kasolution.api.dto.AuthResponse;
import com.kasolution.api.dto.UsuarioResponse;
import com.kasolution.api.exception.CredenciaisException;
import com.kasolution.api.model.Usuario;

import com.kasolution.api.repository.UsuarioRepository;
import com.kasolution.api.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request
    ){

        Usuario user = userRepository.findByEmail(request.email()).orElseThrow(() ->
                new CredenciaisException("Credenciais invalidas")
        ) ;

        if(!passwordEncoder.matches(request.senha(), user.getSenha() )){
            throw new CredenciaisException("Senha não confere");
        }

        if (!user.isActive()){
            throw new CredenciaisException("Usuario esta desativado");
        }

        String token = jwtService.generateToken(user);

        AuthResponse response = AuthResponse.builder()
                .token(token)

                .usuarioResponse(
                        UsuarioResponse.builder()
                                .id(user.getId())
                                .nome(user.getNome())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .gerenteid(user.getGerenteid())
                                .ativo(user.isActive())
                                .build() // termina a criação do objeto usuarioresponse
                )
                .build(); // termina a criação do objeto authresponse

        return ResponseEntity.ok(response);

    }





}
