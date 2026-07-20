package com.defulo.api.features.evento.dto.request;

import com.defulo.api.features.evento.model.TipoEvento;
import jakarta.validation.constraints.*;

/**
 * DTO de entrada para criação de um Evento de Manejo.
 * talhaoId é obrigatório para vincular o evento a um talhão existente.
 */
public record EventoCreateRequestDto(

        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @NotBlank(message = "A descrição é obrigatória.")
        @Size(min = 3, max = 5000, message = "A descrição deve ter entre 3 e 5000 caracteres.")
        String descricao,

        @NotNull(message = "O tipo do evento é obrigatório.")
        TipoEvento tipo,

        @DecimalMin(value = "0.0", inclusive = false, message = "A quantidade deve ser maior que zero.")
        Double quantidadeValor,

        @Size(max = 20, message = "A unidade deve ter no máximo 20 caracteres.")
        String quantidadeUnidade,

        @NotNull(message = "O ID do talhão é obrigatório.")
        Long talhaoId

) {}
