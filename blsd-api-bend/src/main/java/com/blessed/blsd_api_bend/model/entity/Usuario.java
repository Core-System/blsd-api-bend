package com.blessed.blsd_api_bend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    @Column(name = "url_foto")
    private String urlFoto;
    @Column(name = "data_criacao")  
    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "acesso_id")
    private Acesso acesso;

}
