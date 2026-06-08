package com.blessed.blsd_api_bend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cliente extends Usuario{

    @Column(name = "data_nasc")
    private LocalDate dataNasc;

    private String telefone;

    @OneToOne
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

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
