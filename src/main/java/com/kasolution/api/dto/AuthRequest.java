package com.kasolution.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder

public record AuthRequest(
        @Email @NotBlank(message = "O email é obrigatório") String email ,
        @NotBlank(message = "A senha é obrigatória") String senha) {

}

