package com.kasolution.api.dto;

import com.kasolution.api.model.Categoria;

public record CategoriaResponse(

        Long id,
        String nome,
        String hexadecimal
) {
    public CategoriaResponse(Categoria categoria) {
        this(
                categoria.getId(),
                categoria.getNome(),
                categoria.getHexadecimal()
        );
    }
}
