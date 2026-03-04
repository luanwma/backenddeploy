package com.kasolution.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(

        @NotBlank(message = "O nome da categoria é obrigatório")
        String nome,

        @NotBlank(message = "A cor hexadecimal é obrigatória")
        String hexadecimal
) {
}
