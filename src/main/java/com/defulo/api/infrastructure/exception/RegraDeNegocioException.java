package com.defulo.api.infrastructure.exception;

/**
 * Exceção lançada quando uma regra de negócio é violada.
 * Resulta em HTTP 400 Bad Request.
 */
public class RegraDeNegocioException extends RuntimeException {

    /**
     * Cria uma nova instância da exceção.
     *
     * @param mensagem descrição da regra de negócio violada
     */
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
