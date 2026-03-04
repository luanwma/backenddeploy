package com.kasolution.api.security;

import com.kasolution.api.model.Role;

import java.util.UUID;

public record UsuarioAutenticado(UUID id, String email, Role role, UUID gerenteId) {
}
