package com.kasolution.api.dto;

import com.kasolution.api.model.Role;
import jdk.jshell.Snippet;
import lombok.Builder;

import java.util.UUID;
@Builder
public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        Role role,
        UUID gerenteid,
        boolean ativo) {

}
