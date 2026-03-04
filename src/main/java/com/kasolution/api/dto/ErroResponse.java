package com.kasolution.api.dto;

import java.time.Instant;

public record ErroResponse(

        Instant timestamp,
        Integer status,
        String erro,
        String mensagem,
        String path
) {

}
