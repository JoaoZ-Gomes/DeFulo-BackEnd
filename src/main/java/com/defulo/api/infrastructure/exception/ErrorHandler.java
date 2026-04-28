package com.defulo.api.infrastructure.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manipulador global de exceções para a API.
 * Trata todas as exceções não capturadas e retorna respostas HTTP padronizadas
 * com corpo estruturado.
 */
@RestControllerAdvice
public class ErrorHandler {

    // =====================================================
    // VALIDAÇÃO
    // =====================================================

    /**
     * Trata exceções de validação de campos.
     * Lançada quando {@code @Valid} falha em algum campo do RequestBody.
     *
     * @param ex a exceção de validação
     * @return ResponseEntity com mapa de erros de campo e HTTP 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError
                    ? ((FieldError) error).getField()
                    : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("erro", "Erro de validação");
        body.put("campos", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // =====================================================
    // REGRA DE NEGÓCIO
    // =====================================================

    /**
     * Trata exceções de regra de negócio violada.
     * Exemplos: email duplicado, CPF inválido, dados inconsistentes.
     *
     * @param ex a exceção de regra de negócio
     * @return ResponseEntity com mensagem de erro e HTTP 400
     */
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Map<String, Object>> handleRegraDeNegocioException(
            RegraDeNegocioException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("erro", "Regra de negócio violada");
        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // =====================================================
    // RECURSO NÃO ENCONTRADO
    // =====================================================

    /**
     * Trata exceções de recurso não encontrado.
     * Lançada quando uma consulta por ID retorna vazio.
     *
     * @param ex a exceção de recurso não encontrado
     * @return ResponseEntity com mensagem de erro e HTTP 404
     */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoNaoEncontradoException(
            RecursoNaoEncontradoException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("erro", "Recurso não encontrado");
        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // =====================================================
    // AUTENTICAÇÃO
    // =====================================================

    /**
     * Trata exceções de autenticação falha.
     * Lançada quando email ou senha estão incorretos.
     *
     * @param ex a exceção de autenticação
     * @return ResponseEntity com mensagem de erro e HTTP 401
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("erro", "Falha na autenticação");
        body.put("mensagem", "Email ou senha incorretos.");

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
}
