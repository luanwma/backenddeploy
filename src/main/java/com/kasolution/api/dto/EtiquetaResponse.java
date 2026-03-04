package com.kasolution.api.dto;

import com.kasolution.api.model.Etiqueta;

public record EtiquetaResponse(
        Long id,
        String descricao
) {

    public EtiquetaResponse(Etiqueta etiqueta) {
        this(etiqueta.getId(), etiqueta.getDescricao());
    }
}
