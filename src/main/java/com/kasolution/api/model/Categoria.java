package com.kasolution.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;
    @Column(nullable = false)
    private String hexadecimal;

}
