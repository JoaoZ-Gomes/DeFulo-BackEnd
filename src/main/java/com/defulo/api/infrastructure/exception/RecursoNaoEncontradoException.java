package com.defulo.api.infrastructure.exception;

/**
 * Exceção lançada quando um recurso não é encontrado pelo ID.
 * Resulta em HTTP 404 Not Found.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    /**
     * Cria uma nova instância da exceção.
     *
     * @param mensagem descrição do recurso não encontrado
     */
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
