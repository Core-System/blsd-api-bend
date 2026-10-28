package com.blessed.blsd_api_bend.exception.consulta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConsultaNaoConcluidaException extends RuntimeException {
    public ConsultaNaoConcluidaException(String message) {
        super(message);
    }
}
