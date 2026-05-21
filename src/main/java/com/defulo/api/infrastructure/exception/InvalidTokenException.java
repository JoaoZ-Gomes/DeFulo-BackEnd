package com.defulo.api.infrastructure.exception;

/**
 * Exceção lançada quando um token JWT é inválido ou expirado.
 * Resulta em HTTP 401 Unauthorized.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String mensagem) {
        super(mensagem);
    }
}
