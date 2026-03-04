package com.kasolution.api.repository;

import com.kasolution.api.model.Etiqueta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {

    Optional<Etiqueta> findByDescricao(String descricao);
}
