package com.blessed.blsd_api_bend.dto.agendamento;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NotificacaoEmailRequest(
        String destinatario,
        String assunto,
        String mensagem
) {
    public NotificacaoEmailRequest(@JsonProperty("destinatario") String destinatario, @JsonProperty("assunto") String assunto, @JsonProperty("mensagem") String mensagem) {
        this.destinatario = destinatario;
        this.assunto = assunto;
        this.mensagem = mensagem;
    }

}