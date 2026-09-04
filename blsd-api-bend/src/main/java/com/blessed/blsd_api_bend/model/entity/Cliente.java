package com.blessed.blsd_api_bend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cliente extends Usuario {

    @Column(name = "data_nasc")
    private LocalDate dataNasc;

    private String telefone;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.REMOVE)
    private List<Consulta> consultas = new ArrayList<>();

    @Column(name = "token_agendamento")
    private String tokenAgendamento;

    @Column(name = "expiracao_token_agendamento")
    private LocalDateTime expiracaoTokenAgendamento;

    public void gerarToken() {
        this.tokenAgendamento = UUID.randomUUID().toString();
        this.expiracaoTokenAgendamento = LocalDateTime.now().plusHours(24);
    }

    public void invalidarToken() {
        this.tokenAgendamento = null;
        this.expiracaoTokenAgendamento = null;
    }
}