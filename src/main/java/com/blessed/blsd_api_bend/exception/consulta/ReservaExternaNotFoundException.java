package com.blessed.blsd_api_bend.exception.consulta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReservaExternaNotFoundException extends RuntimeException {
    public ReservaExternaNotFoundException(String message) {
        super(message);
    }
}
