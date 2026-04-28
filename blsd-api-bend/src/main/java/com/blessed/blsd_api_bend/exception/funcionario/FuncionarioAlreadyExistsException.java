package com.blessed.blsd_api_bend.exception.funcionario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FuncionarioAlreadyExistsException extends RuntimeException {
    public FuncionarioAlreadyExistsException(String message) {
        super(message);
    }
}
