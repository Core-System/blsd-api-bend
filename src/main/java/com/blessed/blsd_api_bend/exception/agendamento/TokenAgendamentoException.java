package com.blessed.blsd_api_bend.exception.agendamento;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenAgendamentoException extends RuntimeException {
    public TokenAgendamentoException(String message) {
        super(message);
    }
}
