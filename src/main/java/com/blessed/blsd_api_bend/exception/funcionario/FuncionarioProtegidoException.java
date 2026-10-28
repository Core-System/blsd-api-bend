package com.blessed.blsd_api_bend.exception.funcionario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FuncionarioProtegidoException extends RuntimeException {
    public FuncionarioProtegidoException(String message) {
        super(message);
    }
}
