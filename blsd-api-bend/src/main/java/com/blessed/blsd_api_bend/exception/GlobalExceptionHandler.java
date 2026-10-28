package com.blessed.blsd_api_bend.exception;

import com.blessed.blsd_api_bend.exception.agendamento.HorarioIndisponivelException;
import com.blessed.blsd_api_bend.exception.agendamento.TokenAgendamentoException;
import com.blessed.blsd_api_bend.exception.cliente.ClienteAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.cliente.ClienteNotFoundException;
import com.blessed.blsd_api_bend.exception.consulta.ConsultaNaoConcluidaException;
import com.blessed.blsd_api_bend.exception.consulta.ConsultaNotFoundException;
import com.blessed.blsd_api_bend.exception.consulta.ReservaExternaNotFoundException;
import com.blessed.blsd_api_bend.exception.estoque.EstoqueInsuficienteException;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioNotFoundException;
import com.blessed.blsd_api_bend.exception.infraestrutura.ArmazenamentoArquivoException;
import com.blessed.blsd_api_bend.exception.produto.ProdutoNotFoundException;
import com.blessed.blsd_api_bend.exception.servico.ServicoAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.servico.ServicoNotFoundException;
import com.blessed.blsd_api_bend.exception.usuario.UsuarioNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Existem campos inválidos.");
        body.put("fields", fields);
        return ResponseEntity.badRequest().body(body);
    }

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

    @ExceptionHandler({
            ClienteNotFoundException.class,
            FuncionarioNotFoundException.class,
            ProdutoNotFoundException.class,
            com.blessed.blsd_api_bend.exception.estoque.ProdutoNotFoundException.class,
            ServicoNotFoundException.class,
            ConsultaNotFoundException.class,
            ReservaExternaNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFound(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Not found", ex.getMessage());
    }

    @ExceptionHandler({
            ConsultaNaoConcluidaException.class,
            EstoqueInsuficienteException.class,
            TokenAgendamentoException.class,
            ServicoAlreadyExistsException.class
    })
    public ResponseEntity<Object> handleBusinessConflict(RuntimeException ex) {
        HttpStatus status = ex instanceof TokenAgendamentoException
                ? HttpStatus.BAD_REQUEST
                : ex instanceof ServicoAlreadyExistsException
                    ? HttpStatus.CONFLICT
                    : HttpStatus.CONFLICT;
        return buildErrorResponse(status, status.getReasonPhrase(), ex.getMessage());
    }

    @ExceptionHandler(ArmazenamentoArquivoException.class)
    public ResponseEntity<Object> handleFileStorage(ArmazenamentoArquivoException ex) {
        LOGGER.error("Falha no armazenamento de arquivo", ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Não foi possível armazenar o arquivo."
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        String errorReason = status.getReasonPhrase();
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();

        return buildErrorResponse(status, errorReason, message);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Object> handleUsuarioNotFound(UsuarioNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Not found", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleUnexpectedRuntimeException(RuntimeException ex) {
        LOGGER.error("Erro não tratado durante o processamento da requisição", ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Ocorreu um erro interno ao processar a requisição."
        );
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