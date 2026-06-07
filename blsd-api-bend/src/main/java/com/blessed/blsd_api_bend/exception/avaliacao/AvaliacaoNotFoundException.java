package com.blessed.blsd_api_bend.exception.avaliacao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AvaliacaoNotFoundException extends RuntimeException {
    public AvaliacaoNotFoundException(String message) {
        super(message);
    }
}