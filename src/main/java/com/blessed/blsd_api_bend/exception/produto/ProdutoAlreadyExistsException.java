package com.blessed.blsd_api_bend.exception.produto;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProdutoAlreadyExistsException extends RuntimeException {
    public ProdutoAlreadyExistsException(String message) {
        super(message);
    }
}
