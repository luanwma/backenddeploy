package com.kasolution.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String nome;


    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    @Column(nullable = false, updatable = false)
    private Instant createdAt;


    @Builder.Default
    private boolean active = true;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public boolean isGerente() {
        return role == Role.GERENTE;
    }

    public boolean isDesenvolvedor() {
        return role == Role.DESENVOLVEDOR;
    }

    @Column(nullable = true, updatable = false)
    public UUID gerenteid;

//   @OneToMany(mappedBy = "desenvolvedor",fetch = FetchType.LAZY)
//    private List<UserTarefa> tarefas;




}
