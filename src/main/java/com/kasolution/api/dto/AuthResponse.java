package com.kasolution.api.dto;

import lombok.Builder;

@Builder
public record AuthResponse(String token, UsuarioResponse usuarioResponse) {
}

