package com.blessed.blsd_api_bend.exception.servico;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ServicoAlreadyExistsException extends RuntimeException {
    public ServicoAlreadyExistsException(String message) {
        super(message);
    }
}
