package com.kasolution.api.repository;

import com.kasolution.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {


    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);


    @Query("SELECT u FROM Usuario u WHERE u.id = :usuarioId")
    Optional<Usuario> findById(@Param("usuarioId") UUID usuarioId);





}
