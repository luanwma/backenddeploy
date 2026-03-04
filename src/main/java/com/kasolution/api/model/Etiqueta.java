package com.kasolution.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "etiquetas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String descricao;


}
