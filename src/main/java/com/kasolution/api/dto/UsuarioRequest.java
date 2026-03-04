package com.kasolution.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UsuarioRequest(
        @NotBlank(message = "Nome nao pode estar em branco") String nome,
        @NotBlank(message = "Email nao pode estar em branco") @Email String email,
        @NotBlank(message = "Senha é obrigatoria e deve ter entre 6 e 100 caracteres") @Size(min = 6, max = 100) String senha
) {
}
