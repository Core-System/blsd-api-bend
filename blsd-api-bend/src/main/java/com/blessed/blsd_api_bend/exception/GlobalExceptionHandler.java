package com.blessed.blsd_api_bend.exception;

import com.blessed.blsd_api_bend.exception.agendamento.HorarioIndisponivelException;
import com.blessed.blsd_api_bend.exception.cliente.ClienteAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClienteAlreadyExistsException.class)
    public ResponseEntity<Object> handleClienteAlreadyExists(ClienteAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(FuncionarioAlreadyExistsException.class)
    public ResponseEntity<Object> handleFuncionarioAlreadyExists(FuncionarioAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(HorarioIndisponivelException.class)
    public ResponseEntity<Object> handleHorarioIndisponivel(HorarioIndisponivelException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        String errorReason = status.getReasonPhrase();
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();

        return buildErrorResponse(status, errorReason, message);
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}